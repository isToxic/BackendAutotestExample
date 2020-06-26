package ru.msb.common.service;

import io.cucumber.datatable.DataTable;
import io.vavr.CheckedFunction0;
import io.vavr.Tuple;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Header;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.AbstractMessageListenerContainer;
import org.springframework.kafka.listener.ConsumerSeekAware;
import org.springframework.kafka.listener.adapter.FilteringMessageListenerAdapter;
import org.springframework.kafka.listener.adapter.RecordFilterStrategy;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import ru.msb.common.dao.ConsumerRecordsDao;
import ru.msb.common.dao.KafkaStorageDao;

import java.util.*;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

import static io.vavr.control.Try.of;

/**
 * Класс для работы с Каfka, реализованы методы отправки/получения сообщений
 */
@Slf4j
@Service
public class KafkaService  implements ConsumerSeekAware {

    private final KafkaStorageDao kafkaDao;
    private final ConsumerRecordsDao recordsDao;

    @Autowired
    public KafkaService(KafkaStorageDao kafkaDao, ConsumerRecordsDao recordsDao) {
        this.kafkaDao = kafkaDao;
        this.recordsDao = recordsDao;
    }

    public void listenDefaultTimeout(
            @NonNull Tuple storageName,
            @NonNull String topic,
            RecordFilterStrategy<String, String> filterStrategy) {
        listen(storageName, topic, filterStrategy, 3000L);
    }

    public void listen(
            @NonNull Tuple storageName,
            @NonNull String topic,
            RecordFilterStrategy<String, String> filterStrategy) {
        listen(storageName, topic, filterStrategy, null);
    }

    public void send(
            @NonNull Tuple storageName,
            @NonNull String topic,
            @Nullable String key,
            @NonNull String data,
            @Nullable Iterable<Header> headers) {
        KafkaTemplate<String, String> template = kafkaDao.getKafkaStrorage(storageName).getKafkaTemplate();
        send(template, genRecord(template, topic, key, data, headers));
    }

    public List<Header> getHeadersFromDataTable(DataTable headersTable) {
        return of((CheckedFunction0<ArrayList<Header>>) ArrayList::new)
                .andThen(headerList ->
                        headersTable.asMap(String.class, List.class)
                                .forEach((name, value) -> headerList.add(
                                        new Header() {
                                            @Override
                                            public String key() {
                                                return (String) name;
                                            }

                                            @Override
                                            public byte[] value() {
                                                return ((String) value).getBytes();
                                            }
                                        })
                                )
                ).get();
    }

    private void send(
            KafkaTemplate<String, String> template,
            ProducerRecord<String, String> record) {
        of(() -> template.send(record))
                .andThenTry(Future::get)
                .andThen(result -> result.addCallback(
                        res ->
                                log.info("Сообщение:\n{}\n с ключом: {} успешно отправлено в очередь: {}, offset:{}",
                                        record.value(),
                                        Optional.ofNullable(record.key()).orElse(""),
                                        record.topic(),
                                        Objects.requireNonNull(res).getRecordMetadata().offset()),
                        ex ->
                                log.error("Сообщение:\n{}\n не было отправлено в очередь: {}.\n Подробности: {}",
                                        record.value(), record.topic(), ex.getMessage())
                        )
                ).get();
    }

    private void listen(@NonNull Tuple storageName, @NonNull String topic,
                        RecordFilterStrategy<String, String> filterStrategy,
                        @Nullable Long timeout) {
        AtomicBoolean wait = new AtomicBoolean(true);
        String threadName = Thread.currentThread().getName();
        of(() -> getContainerFactory(storageName).createContainer(topic))
                .andThen(listenerContainer -> listenerContainer.setupMessageListener(
                        new FilteringMessageListenerAdapter<>(
                                data -> of(() ->
                                        Try.run(() -> recordsDao.save(data, threadName))
                                                .onSuccess(result -> log.info("Из очереди {} прочитано сообщение: {}",
                                                        topic, data.value())
                                                )
                                                .onFailure(ex -> log.error(ex.getMessage()))
                                                .andThen((Runnable) listenerContainer::stop)
                                                .andThen(() -> wait.set(false))
                                ).get(),
                                filterStrategy,
                                true)
                        )
                ).andThen(AbstractMessageListenerContainer::start)
                .get();
        if (timeout == null) {
            while (wait.get()) {
                Try.run(() -> Thread.sleep(10)).get();
            }
        } else {
            Try.run(() -> Thread.sleep(timeout)).get();
        }
    }

    private ConcurrentKafkaListenerContainerFactory<String, String> getContainerFactory(Tuple storageName) {
        return of((CheckedFunction0<ConcurrentKafkaListenerContainerFactory<String, String>>) ConcurrentKafkaListenerContainerFactory::new)
                .andThen(
                        factory -> factory.setConsumerFactory(kafkaDao.getKafkaStrorage(storageName).getConsumerFactory())
                )
                .andThen(factory -> factory.setAutoStartup(false))
                .andThen(factory -> factory.setConcurrency(4))
                .get();
    }

    private ProducerRecord<String, String> genRecord(
            KafkaTemplate<String, String> template,
            String topic, @Nullable String key, String data,
            @Nullable Iterable<Header> headers) {
        return new ProducerRecord<>(topic, new Random().nextInt(template.partitionsFor(topic).size()), key, data, headers);
    }
}

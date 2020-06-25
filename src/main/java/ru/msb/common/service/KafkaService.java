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
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.ConsumerSeekAware;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.kafka.listener.adapter.RecordFilterStrategy;
import org.springframework.kafka.support.SendResult;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import ru.msb.common.dao.ConsumerRecordsDao;
import ru.msb.common.dao.KafkaStorageDao;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

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
            RecordFilterStrategy<? super byte[], ? super byte[]> filterStrategy) {
        listen(storageName, topic, filterStrategy, 3000L);
    }

    public void listen(
            @NonNull Tuple storageName,
            @NonNull String topic,
            RecordFilterStrategy<? super byte[], ? super byte[]> filterStrategy) {
        listen(storageName, topic, filterStrategy, null);
    }

    public void send(
            @NonNull Tuple storageName,
            @NonNull String topic,
            @Nullable byte[] key,
            @NonNull byte[] data,
            @Nullable Iterable<Header> headers) {
        KafkaTemplate<byte[], byte[]> template = kafkaDao.getKafkaStrorage(storageName).getKafkaTemplate();
        send(template, genRecord(template, topic, key, data, headers));
    }

    public void send(
            @NonNull Tuple storageName,
            @NonNull String topic,
            @Nullable String key,
            @NonNull String data,
            @Nullable Iterable<Header> headers) {
        send(storageName, topic, key != null ? key.getBytes() : null, data.getBytes(), headers);
    }

    public List<Header> getHeadersFromDataTable(DataTable headersTable) {
        return Try.of((CheckedFunction0<ArrayList<Header>>) ArrayList::new)
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

    private ProducerRecord<byte[], byte[]> genRecord(
            KafkaTemplate<byte[], byte[]> template,
            String topic, @Nullable byte[] key, byte[] data,
            @Nullable Iterable<Header> headers) {
        return new ProducerRecord<>(topic, new Random().nextInt(template.partitionsFor(topic).size()), key, data, headers);
    }

    private void send(
            KafkaTemplate<byte[], byte[]> template,
            ProducerRecord<byte[], byte[]> record) {
        ListenableFuture<SendResult<byte[], byte[]>> future = template.send(record);
        Try.of(future::get)
                .onSuccess(result ->
                        log.info("Сообщение:\n{}\n с ключом: {} успешно отправлено в очередь: {}, offset:{}",
                                new String(record.value()),
                                new String(Optional.ofNullable(record.key()).orElse(new byte[0])),
                                record.topic(),
                                result.getRecordMetadata().offset())
                ).onFailure(ex ->
                log.error("Сообщение:\n{}\n не было отправлено в очередь: {}.\n Подробности: {}",
                        new String(record.value()), record.topic(), ex.getMessage())
        );
    }

    private void listen(@NonNull Tuple storageName, @NonNull String topic,
                        RecordFilterStrategy<? super byte[], ? super byte[]> filterStrategy,
                        @Nullable Long timeout) {
        ConcurrentKafkaListenerContainerFactory<byte[], byte[]> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(
                kafkaDao.getKafkaStrorage(storageName).getConsumerFactory()
        );
        factory.setAutoStartup(false);
        factory.setRecordFilterStrategy(filterStrategy);
        factory.setConcurrency(2);
        ConcurrentMessageListenerContainer<byte[], byte[]> listenerContainer =
                factory.createContainer(topic);
        String threadName = Thread.currentThread().getName();
        listenerContainer.setupMessageListener(
                (MessageListener<byte[], byte[]>) data ->
                        Try.of(() ->
                                Try.run(() -> recordsDao.save(data, threadName))
                                        .onSuccess(result -> log.info("Из очереди {} прочитано сообщение: {}",
                                                topic, new String(data.value()))
                                        )
                                        .onFailure(ex -> log.error(ex.getMessage()))
                                        .andThen((Runnable) listenerContainer::stop)
                        ).get()
        );
        listenerContainer.start();
        if (timeout == null) {
            while (listenerContainer.isRunning()) {
                Try.run(() -> Thread.sleep(10)).get();
            }
        } else {
            Try.run(() -> Thread.sleep(timeout)).get();
        }
    }
}

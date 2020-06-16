package ru.msb.common.service;

import io.vavr.Tuple;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Header;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.kafka.listener.adapter.RecordFilterStrategy;
import org.springframework.kafka.support.SendResult;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import ru.msb.common.dao.ConsumerRecordsDao;
import ru.msb.common.dao.KafkaStorageDao;

import java.util.Random;

/**
 * Класс для работы с Каfka, реализованы методы отправки/получения сообщений
 */
@Slf4j
@Service
public class KafkaService {

    private final KafkaStorageDao kafkaDao;
    private final ConsumerRecordsDao recordsDao;

    @Autowired
    public KafkaService(KafkaStorageDao kafkaDao, ConsumerRecordsDao recordsDao) {
        this.kafkaDao = kafkaDao;
        this.recordsDao = recordsDao;
    }

    public Tuple listen(
            @NonNull Tuple storageName,
            @NonNull String topic,
            RecordFilterStrategy<? super byte[], ? super byte[]> filterStrategy) {
        final Tuple[] storageKey = new Tuple[1];
        ConcurrentKafkaListenerContainerFactory<byte[], byte[]> factory =
                kafkaDao.getKafkaStrorage(storageName).getListenerFactory();
        factory.setRecordFilterStrategy(filterStrategy);
        factory.setConcurrency(1);
        ConcurrentMessageListenerContainer<byte[], byte[]> listenerContainer =
                factory.createContainer(topic);
        listenerContainer.setupMessageListener(
                (MessageListener<byte[], byte[]>) data ->
                        Try.run(() ->
                                storageKey[0] = Tuple.of(recordsDao.save(data)))
                                .andThen((Runnable) listenerContainer::stop));
        listenerContainer.start();
        return storageKey[0];
    }

    public void send(
            @NonNull Tuple storageName,
            @NonNull String topic,
            @NonNull byte[] key,
            @NonNull byte[] data,
            @Nullable Iterable<Header> headers) {
        KafkaTemplate<byte[], byte[]> template = kafkaDao.getKafkaStrorage(storageName).getKafkaTemplate();
        send(template, genRecord(template, topic, key, data, headers));
    }

    public void send(
            @NonNull Tuple storageName,
            @NonNull String topic,
            @NonNull String key,
            @NonNull String data,
            @Nullable Iterable<Header> headers) {
        send(storageName, topic, key.getBytes(), data.getBytes(), headers);
    }

    private ProducerRecord<byte[], byte[]> genRecord(
            KafkaTemplate<byte[], byte[]> template,
            String topic, byte[] key, byte[] data,
            @Nullable Iterable<Header> headers) {
        return new ProducerRecord<>(topic, new Random().nextInt(template.partitionsFor(topic).size()), key, data, headers);
    }

    private void send(
            KafkaTemplate<byte[], byte[]> template,
            ProducerRecord<byte[], byte[]> record) {
        ListenableFuture<SendResult<byte[], byte[]>> future = template.send(record);
        future.addCallback(new ListenableFutureCallback<>() {

            @Override
            public void onSuccess(SendResult<byte[], byte[]> result) {
                log.info("Сообщение:\n{}\n с ключом: {} успешно отправлено в очередь: {}, offset:{}",
                        new String(record.value()), new String(record.key()), record.topic(), result.getRecordMetadata().offset());
            }

            @Override
            public void onFailure(@NonNull Throwable ex) {
                log.error("Сообщение:\n{}\n не было отправлено в очередь: {}.\n Подробности: {}",
                        new String(record.value()), record.topic(), ex.getMessage());
            }
        });
    }
}

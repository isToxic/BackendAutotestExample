package ru.msb.common.repository;

import io.vavr.Tuple;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;
import ru.msb.common.dao.ByteArrayContentDao;
import ru.msb.common.dao.ConsumerRecordsDao;
import ru.msb.common.dao.ResponseEntityDao;
import ru.msb.common.dao.StringContentDao;

import java.util.Collection;
import java.util.Optional;

import static ru.msb.common.Common.*;

@Slf4j
@Repository
public class MessagesRepository implements ConsumerRecordsDao, ResponseEntityDao,
        StringContentDao, ByteArrayContentDao {
    @Override
    public ConsumerRecord<String, String> getConsumerRecord(Tuple key) {
        return Optional.ofNullable(
                CONSUMER_RECORD_CONCURRENT_MAP.getOrDefault(key, null))
                .orElseThrow(
                        () -> new IllegalArgumentException(
                                String.format(
                                        "No ConsumerRecord with name: %s",
                                        key.toString()
                                )
                        )
                );
    }

    @Override
    public Collection<ConsumerRecord<String, String>> getAllConsumerRecords() {
        return CONSUMER_RECORD_CONCURRENT_MAP.values();
    }

    @Override
    public void save(ConsumerRecord<String, String> consumerRecord, @Nullable String mainThreadName) {
        CONSUMER_RECORD_CONCURRENT_MAP.put(
                generateConsumerRecordKey(consumerRecord.topic(), mainThreadName),
                consumerRecord
        );
    }

    @Override
    public ResponseEntity<String> getResponseEntity(Tuple key) {
        return Optional.ofNullable(
                RESPONSE_ENTITY_CONCURRENT_MAP.getOrDefault(key, null))
                .orElseThrow(
                        () -> new IllegalArgumentException(
                                String.format(
                                        "No ResponseEntity with name: %s",
                                        key.toString()
                                )
                        )
                );
    }

    @Override
    public Collection<ResponseEntity<String>> getAllResponseEntities() {
        return RESPONSE_ENTITY_CONCURRENT_MAP.values();
    }

    @Override
    public void save(ResponseEntity<String> responseEntity, String requestName) {
        log.debug("Сохранено сообщение:\n{}", responseEntity.getBody());
        RESPONSE_ENTITY_CONCURRENT_MAP.put(
                generateResponseEntityKey(requestName),
                responseEntity
        );
    }

    @Override
    public String getString(Tuple name) {
        return Optional.ofNullable(
                STRING_CONCURRENT_MAP.getOrDefault(name, null))
                .orElseThrow(
                        () -> new IllegalArgumentException(
                                String.format(
                                        "No String with name: %s",
                                        name.toString()
                                )
                        )
                );
    }

    @Override
    public Collection<String> getAllStrings() {
        return STRING_CONCURRENT_MAP.values();
    }

    @Override
    public Tuple save(String valueType, String value) {
        Tuple key = generateStringKey(valueType);
        STRING_CONCURRENT_MAP.put(key, value);
        return key;
    }

    @Override
    public byte[] getByteArray(Tuple name) {
        return Optional.ofNullable(
                BYTE_ARRAY_CONCURRENT_MAP.getOrDefault(name, null))
                .orElseThrow(
                        () -> new IllegalArgumentException(
                                String.format(
                                        "No ByteArray with name: %s",
                                        name.toString()
                                )
                        )
                );
    }

    @Override
    public Collection<byte[]> getAllByteArrays() {
        return BYTE_ARRAY_CONCURRENT_MAP.values();
    }

    @Override
    public void save(String requestName, String threadName, byte[] value) {
        BYTE_ARRAY_CONCURRENT_MAP.put(generateByteArrayKey(requestName, threadName), value);
    }
}

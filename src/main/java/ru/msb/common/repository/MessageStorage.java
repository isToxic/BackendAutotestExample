package ru.msb.common.repository;

import io.vavr.Tuple;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import ru.msb.common.dao.ConsumerRecordsDao;
import ru.msb.common.dao.ResponseEntityDao;
import ru.msb.common.dao.StringContentDao;

import java.util.Collection;
import java.util.Optional;

import static ru.msb.common.Common.*;

@Repository
public class MessageStorage implements
        ConsumerRecordsDao, ResponseEntityDao, StringContentDao {
    @Override
    public ConsumerRecord<byte[], byte[]> getConsumerRecord(Tuple key) {
        return Optional.of(CONSUMER_RECORD_CONCURRENT_MAP.get(key))
                .orElseThrow();
    }

    @Override
    public Collection<ConsumerRecord<byte[], byte[]>> getAllConsumerRecords() {
        return CONSUMER_RECORD_CONCURRENT_MAP.values();
    }

    @Override
    public Tuple save(ConsumerRecord<byte[], byte[]> consumerRecord) {
        Tuple key = generateConsumerRecordKey(consumerRecord);
        CONSUMER_RECORD_CONCURRENT_MAP.put(key, consumerRecord);
        return key;
    }

    @Override
    public ResponseEntity<String> getResponseEntity(Tuple key) {
        return Optional.of(RESPONSE_ENTITY_CONCURRENT_MAP.get(key))
                .orElseThrow();
    }

    @Override
    public Collection<ResponseEntity<String>> getAllResponseEntities() {
        return RESPONSE_ENTITY_CONCURRENT_MAP.values();
    }

    @Override
    public void save(ResponseEntity<String> responseEntity, String requestName) {
        RESPONSE_ENTITY_CONCURRENT_MAP.put(
                generateResponseEntityKey(requestName),
                responseEntity
        );
    }

    @Override
    public String getString(Tuple name) {
        return Optional.of(STRING_CONCURRENT_MAP.get(name))
                .orElseThrow();
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
}

package ru.msb.common.repository;


import io.vavr.Tuple;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Repository
public interface ConsumerRecordsRepository {
    ConcurrentMap<Tuple, ConsumerRecord<String, String>> CONSUMER_RECORD_CONCURRENT_MAP = new ConcurrentHashMap<>();

    ConsumerRecord<String, String> getConsumerRecord(Tuple key);

    Collection<ConsumerRecord<String, String>> getAllConsumerRecords();

    void save(ConsumerRecord<String, String> consumerRecord,@Nullable String mainThreadName);

}

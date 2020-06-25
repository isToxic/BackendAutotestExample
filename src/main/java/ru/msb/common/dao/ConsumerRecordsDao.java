package ru.msb.common.dao;


import io.vavr.Tuple;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Repository
public interface ConsumerRecordsDao {
    ConcurrentMap<Tuple, ConsumerRecord<byte[], byte[]>> CONSUMER_RECORD_CONCURRENT_MAP = new ConcurrentHashMap<>();

    ConsumerRecord<byte[], byte[]> getConsumerRecord(Tuple key);

    Collection<ConsumerRecord<byte[], byte[]>> getAllConsumerRecords();

    void save(ConsumerRecord<byte[], byte[]> consumerRecord,@Nullable String mainThreadName);

}

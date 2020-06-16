package ru.msb.common.dao;


import io.vavr.Tuple;
import org.springframework.stereotype.Repository;
import ru.msb.common.kafka.KafkaStorage;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Repository
public interface KafkaStorageDao {
    ConcurrentMap<Tuple, KafkaStorage> KAFKA_STORAGE_CONCURRENT_MAP = new ConcurrentHashMap<>();

     KafkaStorage getKafkaStrorage(Tuple name);

     Collection<KafkaStorage> getAllKafkaStrorages();

     void save(KafkaStorage storage);
}

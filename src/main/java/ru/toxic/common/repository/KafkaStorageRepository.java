package ru.toxic.common.repository;

import io.vavr.Tuple;
import org.springframework.stereotype.Repository;
import ru.toxic.common.kafka.KafkaStorage;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Описание хранилища KafkaStorage
 */
@Repository
public interface KafkaStorageRepository {
    ConcurrentMap<Tuple, KafkaStorage> KAFKA_STORAGE_CONCURRENT_MAP = new ConcurrentHashMap<>();

    KafkaStorage getKafkaStrorage(Tuple name);

    Collection<KafkaStorage> getAllKafkaStrorages();

    void save(KafkaStorage storage);
}

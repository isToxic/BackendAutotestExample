package ru.msb.common.repository;

import io.vavr.Tuple;
import org.springframework.stereotype.Repository;
import ru.msb.common.dao.KafkaStorageDao;
import ru.msb.common.kafka.KafkaStorage;

import java.util.Collection;
import java.util.Optional;

@Repository
public class KafkaStorageRepository implements KafkaStorageDao {

    @Override
    public KafkaStorage getKafkaStrorage(Tuple name) {
        return Optional.ofNullable(KAFKA_STORAGE_CONCURRENT_MAP.get(name))
                .orElseThrow(
                        () -> new IllegalArgumentException(
                                String.format(
                                        "No storage with name: %s",
                                        name.toString()
                                )
                        )
                );
    }

    @Override
    public Collection<KafkaStorage> getAllKafkaStrorages() {
        return KAFKA_STORAGE_CONCURRENT_MAP.values();
    }

    @Override
    public void save(KafkaStorage storage) {
        KAFKA_STORAGE_CONCURRENT_MAP.put(storage.getName(), storage);
    }

}

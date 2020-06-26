package ru.msb.common.kafka;

import io.vavr.Tuple;
import lombok.Builder;
import lombok.Data;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

@Data
@Builder
public class KafkaStorage {
    private Tuple name;
    private KafkaTemplate<String, String> kafkaTemplate;
    private DefaultKafkaConsumerFactory<String, String> consumerFactory;

    public static Tuple genKafkaStorageName(@NonNull String brokerName, @Nullable String sslName) {
        return sslName == null ? Tuple.of(brokerName) : Tuple.of(brokerName, sslName);
    }
}

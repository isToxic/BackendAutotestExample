package ru.toxic.common.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import ru.toxic.common.models.KafkaInfo;
import ru.toxic.common.models.SSLStoreInfo;
import ru.toxic.common.repository.KafkaStorageRepository;
import ru.toxic.common.setting.ProjectSettings;

import java.util.HashMap;
import java.util.Map;

import static org.apache.kafka.clients.CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.*;
import static org.apache.kafka.clients.producer.ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG;
import static org.apache.kafka.clients.producer.ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG;
import static org.apache.kafka.common.config.SslConfigs.*;

/**
 * Конфигурация подключения к Kafka
 */
@Slf4j
@EnableKafka
@Configuration
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class KafkaConfig {

    private final ProjectSettings prop;
    private final KafkaStorageRepository repository;

    /**
     * Генерация клиентов для вычитки/записи сообщений в Kafka и сохранение в репозитории
     */
    @Bean
    public void generateStorages() {
        prop.getKafka()
                .forEach((name, settings) -> {
                            repository.save(
                                    KafkaStorage.builder()
                                            .kafkaTemplate(getKafkaTemplate(settings, null))
                                            .consumerFactory(getConsumerFactory(settings, null))
                                            .name(KafkaStorage.genKafkaStorageName(name, null))
                                            .build());
                            log.debug("Добавлена конфигурация для брокера Кафка: {}, ssl: не используется", name);
                            if (settings.getSslStores() != null) {
                                settings.getSslStores()
                                        .forEach(sslUser -> {
                                                    repository.save(
                                                            KafkaStorage.builder()
                                                                    .kafkaTemplate(getKafkaTemplate(settings, sslUser))
                                                                    .consumerFactory(getConsumerFactory(settings, sslUser))
                                                                    .name(KafkaStorage.genKafkaStorageName(name, sslUser))
                                                                    .build());
                                                    log.debug("Добавлена конфигурация для брокера Кафка: {}, ssl: {}", name, sslUser);
                                                }
                                        );
                            }
                        }
                );
    }

    private KafkaTemplate<String, String> getKafkaTemplate(@NonNull KafkaInfo setting, @Nullable String sslStorename) {
        return new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(getConfigs(setting, prop.getSslStores().get(sslStorename))));
    }

    private DefaultKafkaConsumerFactory<String, String> getConsumerFactory(@NonNull KafkaInfo setting, @Nullable String sslStorename) {
        return new DefaultKafkaConsumerFactory<>(getConfigs(setting, prop.getSslStores().get(sslStorename)));
    }

    private Map<String, Object> getConfigs(@NonNull KafkaInfo setting, @Nullable SSLStoreInfo sslStoreInfo) {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(BOOTSTRAP_SERVERS_CONFIG, setting.getHost());
        configProps.put(GROUP_ID_CONFIG, setting.getGroupId());
        configProps.put(AUTO_COMMIT_INTERVAL_MS_CONFIG, setting.getAutoCommitInterval());
        configProps.put(MAX_POLL_INTERVAL_MS_CONFIG, setting.getMaxPollInterval());
        configProps.put(MAX_POLL_RECORDS_CONFIG, setting.getMaxPollRecords());
        configProps.put(KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        configProps.put(VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        configProps.put(KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        if (sslStoreInfo != null) {
            configProps.put(SSL_PROTOCOL_CONFIG, "SSL");
            configProps.put(SSL_TRUSTSTORE_TYPE_CONFIG, sslStoreInfo.getTruststoreType());
            configProps.put(SSL_TRUSTSTORE_LOCATION_CONFIG, sslStoreInfo.getTruststoreLocation());
            configProps.put(SSL_TRUSTSTORE_PASSWORD_CONFIG, sslStoreInfo.getTruststorePassword());
            configProps.put(SSL_KEYSTORE_TYPE_CONFIG, sslStoreInfo.getKeystoreType());
            configProps.put(SSL_KEYSTORE_LOCATION_CONFIG, sslStoreInfo.getTruststoreLocation());
            configProps.put(SSL_KEYSTORE_PASSWORD_CONFIG, sslStoreInfo.getTruststorePassword());
        }
        return configProps;
    }
}

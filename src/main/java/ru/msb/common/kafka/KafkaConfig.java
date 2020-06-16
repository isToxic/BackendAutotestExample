package ru.msb.common.kafka;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.config.SslConfigs;
import org.apache.kafka.common.serialization.ByteArrayDeserializer;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import ru.msb.common.dao.KafkaStorageDao;
import ru.msb.common.setting.ProjectSettings;
import ru.msb.common.models.KafkaInfo;
import ru.msb.common.models.SSLStoreInfo;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@EnableKafka
@Configuration
public class KafkaConfig {

    private final ProjectSettings prop;
    private final KafkaStorageDao repository;

    @Autowired
    public KafkaConfig(ProjectSettings prop, KafkaStorageDao repository) {
        this.prop = prop;
        this.repository = repository;
    }

    @Bean
    public void generateStorages() {
        prop.getKafka()
                .forEach((name, settings) ->
                        {
                            repository.save(
                                    KafkaStorage.builder()
                                            .kafkaTemplate(getKafkaTemplate(settings, null))
                                            .listenerFactory(getListenerContainer(settings, null))
                                            .name(KafkaStorage.genKafkaStorageName(name, null))
                                            .build());
                            log.debug("Добавлена конфигурация для брокера Кафка: {}, ssl: не используется", name);
                            if (settings.getSslStores() != null) {
                                settings.getSslStores()
                                        .forEach(sslUser -> {
                                                    repository.save(
                                                            KafkaStorage.builder()
                                                                    .kafkaTemplate(getKafkaTemplate(settings, sslUser))
                                                                    .listenerFactory(getListenerContainer(settings, sslUser))
                                                                    .name(KafkaStorage.genKafkaStorageName(name, sslUser))
                                                                    .build());
                                                    log.debug("Добавлена конфигурация для брокера Кафка: {}, ssl: {}", name, sslUser);
                                                }
                                        );
                            }
                        }
                );
    }

    private KafkaTemplate<byte[], byte[]> getKafkaTemplate(@NonNull KafkaInfo setting, @Nullable String sslStorename) {
        return new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(getConfigs(setting, prop.getSslStores().get(sslStorename))));
    }

    private ConcurrentKafkaListenerContainerFactory<byte[], byte[]> getListenerContainer(@NonNull KafkaInfo setting, @Nullable String sslStorename) {
        ConcurrentKafkaListenerContainerFactory<byte[], byte[]> result = new ConcurrentKafkaListenerContainerFactory<>();
        result.setConsumerFactory(new DefaultKafkaConsumerFactory<>(getConfigs(setting, prop.getSslStores().get(sslStorename))));
        result.setAutoStartup(false);
        return result;
    }

    private Map<String, Object> getConfigs(@NonNull KafkaInfo setting, @Nullable SSLStoreInfo sslStoreInfo) {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(org.apache.kafka.clients.producer.ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, setting.getHost());
        configProps.put(org.apache.kafka.clients.consumer.ConsumerConfig.GROUP_ID_CONFIG, setting.getGroupId());
        configProps.put(org.apache.kafka.clients.consumer.ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, ByteArrayDeserializer.class);
        configProps.put(org.apache.kafka.clients.consumer.ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ByteArrayDeserializer.class);
        configProps.put(org.apache.kafka.clients.producer.ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, ByteArraySerializer.class);
        configProps.put(org.apache.kafka.clients.producer.ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, ByteArraySerializer.class);
        if (sslStoreInfo != null) {
            configProps.put(SslConfigs.SSL_PROTOCOL_CONFIG, "SSL");
            configProps.put(SslConfigs.SSL_TRUSTSTORE_TYPE_CONFIG, sslStoreInfo.getTruststoreType());
            configProps.put(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG, sslStoreInfo.getTruststoreLocation());
            configProps.put(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG, sslStoreInfo.getTruststorePassword());
            configProps.put(SslConfigs.SSL_KEYSTORE_TYPE_CONFIG, sslStoreInfo.getKeystoreType());
            configProps.put(SslConfigs.SSL_KEYSTORE_LOCATION_CONFIG, sslStoreInfo.getTruststoreLocation());
            configProps.put(SslConfigs.SSL_KEYSTORE_PASSWORD_CONFIG, sslStoreInfo.getTruststorePassword());
        }
        return configProps;
    }
}

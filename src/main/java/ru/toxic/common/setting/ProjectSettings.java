package ru.toxic.common.setting;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import ru.toxic.common.models.KafkaInfo;
import ru.toxic.common.models.RestRequestInfo;
import ru.toxic.common.models.SSLStoreInfo;
import ru.toxic.common.models.WebSocketClientInfo;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Конфигурация propperties проекта и их структура
 */
@Getter
@Setter
@Configuration
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "project")
public class ProjectSettings {
    private Map<String, KafkaInfo> kafka = new LinkedHashMap<>();
    private Map<String, RestRequestInfo> rest = new LinkedHashMap<>();
    private Map<String, WebSocketClientInfo> wss = new LinkedHashMap<>();
    private Map<String, SSLStoreInfo> sslStores = new LinkedHashMap<>();

}

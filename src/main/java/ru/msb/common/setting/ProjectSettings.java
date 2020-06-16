package ru.msb.common.setting;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import ru.msb.common.models.KafkaInfo;
import ru.msb.common.models.RestRequestInfo;
import ru.msb.common.models.SSLStoreInfo;
import java.util.LinkedHashMap;
import java.util.Map;

@Getter
@Setter
@Configuration
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "project")
public class ProjectSettings {
    private Map<String, KafkaInfo> kafka = new LinkedHashMap<>();
    private Map<String, RestRequestInfo> rest = new  LinkedHashMap<>();
    private Map<String, SSLStoreInfo> sslStores = new  LinkedHashMap<>();

}

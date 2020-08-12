package is.toxic.common.models;

import lombok.Data;
import org.springframework.stereotype.Component;

/**
 * Структура конфигурации REST .yml файла
 */
@Data
@Component
public class RestRequestInfo {
    private String host;
    private String port;
    private String mapping;
    private String sslStore;
    private String trustStrategy;
}

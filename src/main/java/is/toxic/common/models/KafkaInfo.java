package is.toxic.common.models;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Структура конфигурации Кафка .yml файла
 */
@Data
@Component
public class KafkaInfo {

    private String host;
    private String groupId;
    private Integer autoCommitInterval;
    private Integer maxPollInterval;
    private Integer maxPollRecords;
    private List<String> sslStores;

}

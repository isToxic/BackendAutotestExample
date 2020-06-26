package ru.msb.common.models;

import lombok.Data;
import org.apache.kafka.common.protocol.types.Field;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Класс-структура конфигурации Кафка .yml файла
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

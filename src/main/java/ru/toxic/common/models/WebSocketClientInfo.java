package ru.toxic.common.models;

import lombok.Data;
import org.springframework.stereotype.Component;

/**
 * Структура конфигурации WS .yml файла
 */
@Data
@Component
public class WebSocketClientInfo {

    private String url;
    private String mapping;
    private Boolean secure;
    private String sslStore;
}

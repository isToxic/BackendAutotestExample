package ru.msb.common.models;

import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
public class WebSocketClientInfo {

    private String url;
    private String mapping;
    private Boolean secure;
    private String sslStore;
}

package ru.msb.common.models;

import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
public class RestRequestInfo {
    private String host;
    private Integer port;
    private String mapping;
    private String sslStore;
    private String trustStrategy;
}

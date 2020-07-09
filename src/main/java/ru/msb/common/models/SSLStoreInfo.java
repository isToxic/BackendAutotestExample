package ru.msb.common.models;

import lombok.Data;
import org.springframework.stereotype.Component;

/**
 * Структура конфигурации ssl для пользователей .yml файла
 */
@Data
@Component
public class SSLStoreInfo {
    private String truststoreType;
    private String keystoreType;
    private String truststoreLocation;
    private String keystoreLocation;
    private String truststorePassword;
    private String keystorePassword;

}

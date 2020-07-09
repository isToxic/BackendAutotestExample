package ru.toxic.common.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import ru.toxic.common.setting.YamlProppertySourceFactory;

/**
 * Конфигурация профиля second
 */
@Profile("second")
@EnableConfigurationProperties
@ComponentScan(basePackages = "ru.toxic")
@PropertySource(value = "file:config\\secondStandConfig.yml", factory = YamlProppertySourceFactory.class)
public class ConfigSecondStand {
}
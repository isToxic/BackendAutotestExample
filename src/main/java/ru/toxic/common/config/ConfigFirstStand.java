package ru.toxic.common.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import ru.toxic.common.setting.YamlProppertySourceFactory;

/**
 * Конфигурация профиля first
 */
@Profile("first")
@EnableConfigurationProperties
@ComponentScan(basePackages = "ru.toxic")
@PropertySource(value = "file:config\\firstStandConfig.yml", factory = YamlProppertySourceFactory.class)
public class ConfigFirstStand {
}

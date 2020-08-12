package is.toxic.common.config;

import is.toxic.common.setting.YamlProppertySourceFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;

/**
 * Конфигурация профиля second
 */
@Profile("second")
@EnableConfigurationProperties
@ComponentScan(basePackages = "ru.toxic")
@PropertySource(value = "file:config\\secondStandConfig.yml", factory = YamlProppertySourceFactory.class)
public class ConfigSecondStand {
}

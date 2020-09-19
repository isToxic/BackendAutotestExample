package is.toxic.common.config;

import is.toxic.common.setting.YamlProppertySourceFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;

/**
 * Конфигурация профиля first
 */
@Profile("first")
@EnableConfigurationProperties
@ComponentScan(basePackages = "is.toxic")
@PropertySource(value = "file:config/firstStandConfig.yml", factory = YamlProppertySourceFactory.class)
public class ConfigFirstStand {
}

package ru.toxic.common.setting;

import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PropertySourceFactory;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Objects;
import java.util.Properties;

/**
 * Фабрика для парсинга .yml конфигурации
 */
public class YamlProppertySourceFactory implements PropertySourceFactory {

    @NonNull
    @Override
    public PropertySource<?> createPropertySource(@Nullable String name, @NonNull EncodedResource resource) throws IOException {
        return new PropertiesPropertySource(
                name != null ? name : Objects.requireNonNull(resource.getResource().getFilename()),
                loadYamlIntoProperties(resource)
        );
    }

    @NonNull
    private Properties loadYamlIntoProperties(EncodedResource resource) throws FileNotFoundException {
        try {
            YamlPropertiesFactoryBean factory = new YamlPropertiesFactoryBean();
            factory.setResources(resource.getResource());
            factory.afterPropertiesSet();
            return Objects.requireNonNull(factory.getObject());
        } catch (IllegalStateException e) {
            // for ignoreResourceNotFound
            Throwable cause = e.getCause();
            if (cause instanceof FileNotFoundException)
                throw (FileNotFoundException) e.getCause();
            throw e;
        }
    }
}
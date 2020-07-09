package ru.toxic.common.config;

import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.test.context.ContextConfiguration;

/**
 * Общая конфигурация проекта
 */
@ContextConfiguration(classes = {ConfigSecondStand.class, ConfigFirstStand.class})
@CucumberContextConfiguration
public class ProjectConfig {
}

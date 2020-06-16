package ru.msb.common.config;

import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration(classes = { ConfigSecondStand.class, ConfigFirstStand.class})
@CucumberContextConfiguration
public class ProjectConfig {
}

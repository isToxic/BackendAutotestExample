package ru.toxic;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        features ="src\\main\\resources\\features",
        extraGlue = "ru.toxic",
        plugin = "pretty")
public class CucumberTest {
}


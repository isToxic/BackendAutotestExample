package ru.msb.stepdefs;

import io.cucumber.java8.Ru;
import org.springframework.beans.factory.annotation.Autowired;
import ru.msb.common.kafka.KafkaStorage;
import ru.msb.common.service.KafkaService;

public class Test implements Ru  {

    @Autowired
    private KafkaService service;

    public Test() {
        Допустим("шаг 1", () -> service.send(KafkaStorage.genKafkaStorageName("broker2", null), "topic1", "key1", "dfgdg", null));
    }
}

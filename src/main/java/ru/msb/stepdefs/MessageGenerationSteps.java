package ru.msb.stepdefs;

import io.cucumber.java8.Ru;
import org.springframework.beans.factory.annotation.Autowired;
import ru.msb.common.Common;
import ru.msb.common.repository.TestCache;
import ru.msb.common.service.JsonProcessingService;

import static ru.msb.common.Common.REST_STRING_REQUEST_MESSAGE;

public class MessageGenerationSteps implements Ru {

    @Autowired
    private JsonProcessingService service;

    @Autowired
    private TestCache storage;

    public MessageGenerationSteps() {
        Допустим("генерирую сообщение по схеме {string} для REST запроса",
                (String fileName) -> storage.save(REST_STRING_REQUEST_MESSAGE,
                        service.generateJsonFromSchema(
                                service.toString(
                                        service.readSchema(fileName))
                        )
                )
        );
        Допустим("генерирую сообщение по схеме {string} для запроса Kafka",
                (String fileName) -> storage.save(Common.KAFKA_STRING_REQUEST_MESSAGE,
                        service.generateJsonFromSchema(
                                service.toString(
                                        service.readSchema(fileName))
                        )
                )
        );
        Допустим("генерирую сообщение по схеме {string} для запроса ws",
                (String fileName) -> storage.save(Common.WS_STRING_REQUEST_MESSAGE,
                        service.generateJsonFromSchema(
                                service.toString(
                                        service.readSchema(fileName))
                        )
                )
        );
    }
}

package is.toxic.stepdefs;

import io.cucumber.java8.Ru;
import is.toxic.common.Common;
import is.toxic.common.repository.TestCache;
import org.springframework.beans.factory.annotation.Autowired;
import is.toxic.common.service.JsonProcessingService;

public class MessageGenerationSteps implements Ru {

    @Autowired
    private JsonProcessingService service;

    @Autowired
    private TestCache storage;

    public MessageGenerationSteps() {
        Допустим("генерирую сообщение по схеме {string} для REST запроса",
                (String fileName) -> storage.save(Common.REST_STRING_REQUEST_MESSAGE,
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

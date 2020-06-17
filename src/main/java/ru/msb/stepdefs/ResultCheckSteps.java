package ru.msb.stepdefs;

import io.cucumber.java8.Ru;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import ru.msb.common.Common;
import ru.msb.common.repository.MessageStorage;
import ru.msb.common.service.JsonProcessingService;

import static ru.msb.common.Common.generateResponseEntityKey;

@Slf4j
public class ResultCheckSteps implements Ru {

    @Autowired
    private JsonProcessingService service;

    @Autowired
    private MessageStorage storage;

    public ResultCheckSteps() {
        Допустим("проверяю, что ответ на запрос {string} соответствует {string} схеме", (String requestName, String fileName) ->
                service.validateIt(
                        service.readSchema(fileName),
                        service.toJson(
                                storage.getResponseEntity(generateResponseEntityKey(requestName))
                                        .getBody())
                )
        );
        Допустим("вывести результат по запросу {string} в консоль", (String requestName)-> {
                    ResponseEntity<String> responseEntity = storage.getResponseEntity(Common.generateResponseEntityKey(requestName));
                    log.info(String.valueOf(responseEntity.getStatusCodeValue()));
                    log.info(responseEntity.getBody());
                });
    }
}

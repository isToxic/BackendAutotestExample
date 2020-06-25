package ru.msb.stepdefs;

import io.cucumber.java8.Ru;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import ru.msb.common.dao.ConsumerRecordsDao;
import ru.msb.common.dao.ResponseEntityDao;
import ru.msb.common.service.JsonProcessingService;

import static ru.msb.common.Common.generateConsumerRecordKey;
import static ru.msb.common.Common.generateResponseEntityKey;

@Slf4j
public class ResultCheckSteps implements Ru {

    @Autowired
    private JsonProcessingService service;

    @Autowired
    private ResponseEntityDao responseEntityDao;

    @Autowired
    private ConsumerRecordsDao consumerRecordsDao;

    public ResultCheckSteps() {
        Допустим("проверяю, что ответ на запрос {string} соответствует {string} схеме", (String requestName, String fileName) ->
                service.validateIt(
                        service.readSchema(fileName),
                        service.toJson(
                                responseEntityDao.getResponseEntity(generateResponseEntityKey(requestName))
                                        .getBody())
                )
        );
        Допустим("проверяю, что ответ из очереди {string} соответствует {string} схеме", (String topic, String fileName) ->
                service.validateIt(
                        service.readSchema(fileName),
                        service.toJson(
                                new String(
                                        consumerRecordsDao.getConsumerRecord(
                                                generateConsumerRecordKey(topic, Thread.currentThread().getName()))
                                                .value()
                                )
                        )
                )
        );
        Допустим("вывести результат по запросу {string} в консоль", (String requestName) -> {
            ResponseEntity<String> responseEntity = responseEntityDao
                    .getResponseEntity(generateResponseEntityKey(requestName));
            log.info(String.valueOf(responseEntity.getStatusCodeValue()));
            log.info(responseEntity.getBody());
        });
    }
}

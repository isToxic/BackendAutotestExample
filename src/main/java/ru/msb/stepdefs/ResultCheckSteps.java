package ru.msb.stepdefs;

import io.cucumber.datatable.DataTable;
import io.cucumber.java8.Ru;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import ru.msb.common.dao.ByteArrayContentDao;
import ru.msb.common.dao.ConsumerRecordsDao;
import ru.msb.common.dao.ResponseEntityDao;
import ru.msb.common.service.JsonProcessingService;

import java.util.List;

import static java.lang.String.valueOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static ru.msb.common.Common.*;

@Slf4j
public class ResultCheckSteps implements Ru {

    @Autowired
    private JsonProcessingService service;

    @Autowired
    private ResponseEntityDao responseEntityDao;

    @Autowired
    private ConsumerRecordsDao consumerRecordsDao;

    @Autowired
    private ByteArrayContentDao byteArrayContentDao;

    @SuppressWarnings(value = "unchecked")
    public ResultCheckSteps() {
        Допустим("проверяю, что ответ на REST запрос {string} соответствует {string} схеме", (String requestName, String fileName) ->
                assertTrue(service.validateIt(
                        service.readSchema(fileName),
                        service.toJson(
                                responseEntityDao.getResponseEntity(generateResponseEntityKey(requestName))
                                        .getBody())
                ))
        );
        Допустим("проверяю, что ответ на REST запрос {string} получен со статус кодом {int}", (String requestName, String statusCode) ->
                assertEquals(
                        valueOf(responseEntityDao.getResponseEntity(generateResponseEntityKey(requestName)).getStatusCodeValue()),
                        statusCode
                )
        );
        Допустим("проверяю, что ответ на REST запрос {string} получен с успешным статус кодом", (String requestName) ->
                assertTrue(responseEntityDao.getResponseEntity(generateResponseEntityKey(requestName)).getStatusCode().is2xxSuccessful())
        );
        Допустим("проверяю, что ответ на REST запрос {string} содержит заголовки:", (String requestName, DataTable headers) ->
                headers.asMap(String.class, List.class).forEach((headerName, headerValue) ->
                        assertTrue(
                                responseEntityDao.getResponseEntity(generateResponseEntityKey(requestName))
                                        .getHeaders().getValuesAsList((String) headerName).containsAll((List<String>) headerValue)
                        )
                )
        );
        Допустим("проверяю, что ответ из очереди {string} соответствует {string} схеме", (String topic, String fileName) ->
                assertTrue(
                        service.validateIt(
                                service.readSchema(fileName),
                                service.toJson(
                                        consumerRecordsDao.getConsumerRecord(
                                                generateConsumerRecordKey(topic, Thread.currentThread().getName()))
                                                .value()
                                )))
        );
        Допустим("проверяю, что ответ на ws запрос {string} соответствует {string} схеме", (String requestName, String fileName) ->
                assertTrue(service.validateIt(
                        service.readSchema(fileName),
                        service.toJson(new String(
                                byteArrayContentDao.getByteArray(generateByteArrayKey(requestName, Thread.currentThread().getName())))
                        )
                ))
        );
    }
}

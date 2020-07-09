package ru.toxic.stepdefs;

import io.cucumber.datatable.DataTable;
import io.cucumber.java8.Ru;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import ru.toxic.common.repository.ByteArrayContentRepository;
import ru.toxic.common.repository.ConsumerRecordsRepository;
import ru.toxic.common.repository.ResponseEntityRepository;
import ru.toxic.common.service.JsonProcessingService;

import java.util.List;

import static java.lang.String.valueOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static ru.toxic.common.Common.*;

@Slf4j
public class ResultCheckSteps implements Ru {

    @Autowired
    private JsonProcessingService service;

    @Autowired
    private ResponseEntityRepository responseEntityRepository;

    @Autowired
    private ConsumerRecordsRepository consumerRecordsRepository;

    @Autowired
    private ByteArrayContentRepository byteArrayContentRepository;

    @SuppressWarnings(value = "unchecked")
    public ResultCheckSteps() {
        Допустим("проверяю, что ответ на REST запрос {string} соответствует {string} схеме", (String requestName, String fileName) ->
                assertTrue(service.validateIt(
                        service.readSchema(fileName),
                        service.toJson(
                                responseEntityRepository.getResponseEntity(generateResponseEntityKey(requestName))
                                        .getBody())
                ))
        );
        Допустим("проверяю, что ответ на REST запрос {string} получен со статус кодом {int}", (String requestName, String statusCode) ->
                assertEquals(
                        valueOf(responseEntityRepository.getResponseEntity(generateResponseEntityKey(requestName)).getStatusCodeValue()),
                        statusCode
                )
        );
        Допустим("проверяю, что ответ на REST запрос {string} получен с успешным статус кодом", (String requestName) ->
                assertTrue(responseEntityRepository.getResponseEntity(generateResponseEntityKey(requestName)).getStatusCode().is2xxSuccessful())
        );
        Допустим("проверяю, что ответ на REST запрос {string} содержит заголовки:", (String requestName, DataTable headers) ->
                headers.asMap(String.class, List.class).forEach((headerName, headerValue) ->
                        assertTrue(
                                responseEntityRepository.getResponseEntity(generateResponseEntityKey(requestName))
                                        .getHeaders().getValuesAsList((String) headerName).containsAll((List<String>) headerValue)
                        )
                )
        );
        Допустим("проверяю, что ответ из очереди {string} соответствует {string} схеме", (String topic, String fileName) ->
                assertTrue(
                        service.validateIt(
                                service.readSchema(fileName),
                                service.toJson(
                                        consumerRecordsRepository.getConsumerRecord(
                                                generateConsumerRecordKey(topic, Thread.currentThread().getName()))
                                                .value()
                                )))
        );
        Допустим("проверяю, что ответ на ws запрос {string} соответствует {string} схеме", (String requestName, String fileName) ->
                assertTrue(service.validateIt(
                        service.readSchema(fileName),
                        service.toJson(new String(
                                byteArrayContentRepository.getByteArray(generateByteArrayKey(requestName, Thread.currentThread().getName())))
                        )
                ))
        );
    }
}

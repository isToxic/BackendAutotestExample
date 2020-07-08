package ru.msb.stepdefs;

import io.cucumber.datatable.DataTable;
import io.cucumber.java8.Ru;
import org.springframework.beans.factory.annotation.Autowired;
import ru.msb.common.repository.TestCache;
import ru.msb.common.service.KafkaService;

import static ru.msb.common.Common.KAFKA_STRING_REQUEST_MESSAGE;
import static ru.msb.common.Common.generateStringKey;
import static ru.msb.common.kafka.KafkaStorage.genKafkaStorageName;

public class KafkaTransportSteps implements Ru {

    @Autowired
    private TestCache storage;

    @Autowired
    private KafkaService service;

    public KafkaTransportSteps() {
        Допустим("отправляю сообщение в очередь {string}, брокера {string}",
                (String topic, String brokerName) ->
                        service.send(
                                genKafkaStorageName(brokerName, null),
                                topic,
                                null,
                                storage.getString(generateStringKey(KAFKA_STRING_REQUEST_MESSAGE)),
                                null
                        )
        );
        Допустим("отправляю сообщение в очередь {string}, брокера {string} с ключом {string}",
                (String topic, String brokerName, String key) ->
                        service.send(
                                genKafkaStorageName(brokerName, null),
                                topic,
                                key,
                                storage.getString(generateStringKey(KAFKA_STRING_REQUEST_MESSAGE)),
                                null
                        )
        );
        Допустим("отправляю сообщение в очередь {string}, брокера {string} с заголоками:",
                (String topic, String brokerName, DataTable headers) ->
                        service.send(
                                genKafkaStorageName(brokerName, null),
                                topic,
                                null,
                                storage.getString(generateStringKey(KAFKA_STRING_REQUEST_MESSAGE)),
                                service.getHeadersFromDataTable(headers)
                        )
        );
        Допустим("отправляю сообщение в очередь {string}, брокера {string} с ключом {string} и заголовкми:",
                (String topic, String brokerName, String key, DataTable headers) ->
                        service.send(
                                genKafkaStorageName(brokerName, null),
                                topic,
                                key,
                                storage.getString(generateStringKey(KAFKA_STRING_REQUEST_MESSAGE)),
                                service.getHeadersFromDataTable(headers)
                        )
        );
        Допустим("отправляю сообщение, используя сертификат {string}, в очередь {string}, брокера {string}",
                (String ssl, String topic, String brokerName) ->
                        service.send(
                                genKafkaStorageName(brokerName, ssl),
                                topic,
                                null,
                                storage.getString(generateStringKey(KAFKA_STRING_REQUEST_MESSAGE)),
                                null
                        )
        );
        Допустим("отправляю сообщение, используя сертификат {string}, в очередь {string}, брокера {string} с ключом {string}",
                (String ssl, String topic, String brokerName, String key) ->
                        service.send(
                                genKafkaStorageName(brokerName, ssl),
                                topic,
                                key,
                                storage.getString(generateStringKey(KAFKA_STRING_REQUEST_MESSAGE)),
                                null
                        )
        );
        Допустим("отправляю сообщение, используя сертификат {string}, в очередь {string}, брокера {string} с заголоками:",
                (String ssl, String topic, String brokerName, DataTable headers) ->
                        service.send(
                                genKafkaStorageName(brokerName, ssl),
                                topic,
                                null,
                                storage.getString(generateStringKey(KAFKA_STRING_REQUEST_MESSAGE)),
                                service.getHeadersFromDataTable(headers)
                        )
        );
        Допустим("отправляю сообщение, используя сертификат {string}, в очередь {string}, брокера {string} с ключом {string} и заголовкми:",
                (String ssl, String topic, String brokerName, String key, DataTable headers) ->
                        service.send(
                                genKafkaStorageName(brokerName, ssl),
                                topic,
                                key,
                                storage.getString(generateStringKey(KAFKA_STRING_REQUEST_MESSAGE)),
                                service.getHeadersFromDataTable(headers)
                        )
        );
        Допустим("вычитываю сообщение из очереди {string} брокера {string} с ключом {string}", (String topic, String brokerName, String messageKey) ->
                service.listenDefaultTimeout(
                        genKafkaStorageName(brokerName, null),
                        topic,
                        consumerRecord -> !messageKey.equals(consumerRecord.key())
                )
        );
        Допустим("вычитываю сообщение из очереди {string} брокера {string} содержащее {string}", (String topic, String brokerName, String messageValue) ->
                service.listenDefaultTimeout(
                        genKafkaStorageName(brokerName, null),
                        topic,
                        consumerRecord -> !consumerRecord.value().contains(messageValue)
                )
        );
        Допустим("вычитываю сообщение, используя сертификат {string}, из очереди {string} брокера {string} с ключом {string}", (String ssl, String topic, String brokerName, String messageKey) ->
                service.listenDefaultTimeout(
                        genKafkaStorageName(brokerName, ssl),
                        topic,
                        consumerRecord -> !messageKey.equals(consumerRecord.key())
                )
        );
        Допустим("вычитываю сообщение, используя сертификат {string}, из очереди {string} брокера {string} содержащее {string}", (String ssl, String topic, String brokerName, String messageValue) ->
                service.listenDefaultTimeout(
                        genKafkaStorageName(brokerName, ssl),
                        topic,
                        consumerRecord -> !consumerRecord.value().contains(messageValue)
                )
        );
    }
}

package ru.msb.common;

import io.vavr.Tuple;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.lang.NonNull;

import java.net.URI;

@Slf4j
public class Common {

    public static final String KAFKA_STRING_REQUEST_MESSAGE = "KAFKA_STRING_REQUEST_MESSAGE";
    public static final String REST_STRING_REQUEST_MESSAGE = "REST_STRING_REQUEST_MESSAGE";

    public static Tuple generateConsumerRecordKey(@NonNull ConsumerRecord<byte[], byte[]> consumerRecord) {
        Tuple key = Tuple.of(
                consumerRecord.timestamp(),
                consumerRecord.topic(),
                Thread.currentThread().getName()
        );
        log.debug("Сгенерирован ключ для хранения ConsumerRecord: {}", key.toSeq().toString());
        return key;
    }

    public static Tuple generateResponseEntityKey(@NonNull String requestName) {
        Tuple key = Tuple.of(
                requestName,
                Thread.currentThread().getName()
        );
        log.debug("Сгенерирован ключ для хранения ResponseEntity: {}",  key.toSeq().toString());
        return key;
    }

    public static Tuple generateStringKey(@NonNull String valueType) {
        Tuple key = Tuple.of(
                valueType,
                Thread.currentThread().getName()
        );
        log.debug("Сгенерирован ключ для хранения StringContent: {}",  key.toSeq().toString());
        return key;
    }

    public static URI genURI(String host, String mapping, int port) {
        return URI.create(String.format("%s/%s:%s", host, mapping, port));
    }
}

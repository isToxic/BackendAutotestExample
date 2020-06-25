package ru.msb.common;

import io.vavr.Tuple;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.net.URI;

import static java.lang.String.format;
import static java.util.Optional.ofNullable;

@Slf4j
public class Common {

    public static final String KAFKA_STRING_REQUEST_MESSAGE = "KAFKA_STRING_REQUEST_MESSAGE";
    public static final String REST_STRING_REQUEST_MESSAGE = "REST_STRING_REQUEST_MESSAGE";

    public static Tuple generateConsumerRecordKey(@NonNull String topic, @Nullable String mainThreadName) {
        Tuple key = Tuple.of(
                topic,
                mainThreadName != null ? mainThreadName : Thread.currentThread().getName()
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

    public static URI genURI(String host, @Nullable String mapping,@Nullable String port) {
        return URI.create(format(
                "%s/%s:%s",
                host,
                ofNullable(mapping).orElse(""),
                ofNullable(port).orElse(""))
        );
    }
}

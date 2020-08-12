package is.toxic.common;

import io.vavr.Tuple;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.net.URI;

import static java.lang.String.format;
import static java.util.Optional.ofNullable;

/**
 * Общие методы и константы
 */
@Slf4j
public class Common {

    public static final String KAFKA_STRING_REQUEST_MESSAGE = "KAFKA_STRING_REQUEST_MESSAGE";
    public static final String REST_STRING_REQUEST_MESSAGE = "REST_STRING_REQUEST_MESSAGE";
    public static final String WS_STRING_REQUEST_MESSAGE = "WS_STRING_REQUEST_MESSAGE";

    /**
     * Генерация ключа для хранения ConsumerRecord
     *
     * @param topic      очередь брокера Kafka
     * @param ThreadName имя потока выполнения
     * @return Tuple
     */
    public static Tuple generateConsumerRecordKey(@NonNull String topic, @Nullable String ThreadName) {
        Tuple key = Tuple.of(
                topic,
                ThreadName != null ? ThreadName : Thread.currentThread().getName()
        );
        log.debug("Сгенерирован ключ для хранения ConsumerRecord: {}", key.toSeq().toString());
        return key;
    }

    /**
     * Генерация ключа для хранения ResponseEntity
     *
     * @param requestName название коннекта
     * @return Tuple
     */
    public static Tuple generateResponseEntityKey(@NonNull String requestName) {
        Tuple key = Tuple.of(
                requestName,
                Thread.currentThread().getName()
        );
        log.debug("Сгенерирован ключ для хранения ResponseEntity: {}", key.toSeq().toString());
        return key;
    }

    /**
     * Генерация ключа для хранения String
     *
     * @param valueType описание сохраняемого значения
     * @return Tuple
     */
    public static Tuple generateStringKey(@NonNull String valueType) {
        Tuple key = Tuple.of(
                valueType,
                Thread.currentThread().getName()
        );
        log.debug("Сгенерирован ключ для хранения StringContent: {}", key.toSeq().toString());
        return key;
    }

    /**
     * Генерация ключа для хранения byte[]
     *
     * @param requestName название коннекта
     * @param threadName  имя потока выполнения
     * @return Tuple
     */
    public static Tuple generateByteArrayKey(@NonNull String requestName, @NonNull String threadName) {
        Tuple key = Tuple.of(requestName, threadName);
        log.debug("Сгенерирован ключ для хранения ByteArray: {}", key.toSeq().toString());
        return key;
    }

    /**
     * Генерация URI
     *
     * @param host    хост запроса
     * @param mapping маппинг запроса
     * @param port    порт запроса
     * @return URI
     */
    public static URI genURI(String host, @Nullable String mapping, @Nullable String port) {
        return URI.create(format(
                "%s%s%s",
                host,
                ofNullable(mapping).orElse(""),
                port != null ? ":" + port : ""
        ));
    }
}

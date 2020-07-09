package ru.msb.common.service;

import io.netty.handler.ssl.SslContextBuilder;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import ru.msb.common.models.SSLStoreInfo;
import ru.msb.common.models.WebSocketClientInfo;
import ru.msb.common.repository.ByteArrayContentRepository;
import ru.msb.common.setting.ProjectSettings;

import java.io.FileInputStream;
import java.net.URI;
import java.nio.charset.Charset;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Collectors;

/**
 * Сервис для отправки/получения сообщений по Web socket
 */
@Slf4j
@Service
public class WebSocketService {

    private final ByteArrayContentRepository byteArrayContentRepository;
    private final ProjectSettings settings;

    @Autowired
    public WebSocketService(ByteArrayContentRepository byteArrayContentRepository, ProjectSettings settings) {
        this.byteArrayContentRepository = byteArrayContentRepository;
        this.settings = settings;
    }

    /**
     * Отправление и слушание ответа WS
     *
     * @param requestName      название коннекта
     * @param sendingData      отпраляемые данные
     * @param subscriptionTime время подписки на вычитку данных
     */
    public void sendAndSubscribe(String requestName, byte[] sendingData, long subscriptionTime) {
        Try.of(() ->
                Flux.from(sendAndSubscribe(requestName, Thread.currentThread().getName(), sendingData, subscriptionTime)).subscribe())
                .andThen(() -> Try.run(() -> Thread.sleep(subscriptionTime * 1000L + 100L)).get())
                .get();
    }

    private Mono<Void> sendAndSubscribe(String requestName, String threadName, byte[] data, long subscriptionTime) {
        final WebSocketClientInfo info = settings.getWss().get(requestName);
        final SSLStoreInfo ssl = info.getSecure() ? settings.getSslStores().get(info.getSslStore()) : null;
        return (ssl == null ? new ReactorNettyWebSocketClient() :
                new ReactorNettyWebSocketClient(
                        getSecureClient(
                                ssl.getTruststoreLocation(),
                                ssl.getTruststorePassword(),
                                ssl.getKeystoreLocation(),
                                ssl.getKeystorePassword(),
                                requestName
                        )))
                .execute(
                        URI.create(info.getUrl() + info.getMapping()),
                        session -> session.send(
                                Mono.just(session.binaryMessage(dataBufferFactory -> dataBufferFactory.wrap(data)))
                        )
                                .thenMany(session.receive()
                                        .take(Duration.ofSeconds(subscriptionTime))
                                        .map(WebSocketMessage::retain)
                                        .doOnNext(dataBuffer -> {
                                            log.info("Получено сообщение: \n{}\nв сессии: {}", dataBuffer.getPayload().toString(Charset.defaultCharset()), String.format("%s:%s", threadName, session.getId()));
                                            byte[] bytes = new byte[dataBuffer.getPayload().readableByteCount()];
                                            dataBuffer.getPayload().read(bytes);
                                            DataBufferUtils.release(dataBuffer.getPayload());
                                            byteArrayContentRepository.save(requestName, threadName, bytes);
                                        })
                                )
                                .doOnSubscribe(subscriber -> log.info(session.getId() + " подписан на вычитку ответа"))
                                .doFinally(signalType -> log.info(session.getId() + " завершена вычитка ответа"))
                                .then());
    }

    private HttpClient getSecureClient(String trustStorePath, String trustStorePass, String keyStorePath, String keyStorePass, String keyAlias) {
        return Try.of(() -> HttpClient.create().secure(
                sslContextSpec -> Try.of(() ->
                        Try.of(() -> KeyStore.getInstance(KeyStore.getDefaultType())
                        ).andThen(trustStore -> Try.of(() ->
                                KeyStore.getInstance(KeyStore.getDefaultType()))
                                .andThen(keyStore ->
                                        Try.run(() ->
                                                trustStore.load(Try.of(() ->
                                                        new FileInputStream(
                                                                Try.of(() ->
                                                                        ResourceUtils.getFile(trustStorePath)
                                                                ).get())).get(), trustStorePass.toCharArray())).get())
                                .andThen(keyStore ->
                                        Try.run(() ->
                                                keyStore.load(
                                                        Try.of(() ->
                                                                new FileInputStream(
                                                                        Try.of(() -> ResourceUtils.getFile(keyStorePath)
                                                                        ).get())).get(),
                                                        keyStorePass.toCharArray())
                                        ).get())
                                .andThen(keyStore -> Try.of(() ->
                                        Collections.list(Try.of(trustStore::aliases).get())
                                                .stream()
                                                .filter(t -> Try.of(() -> trustStore.isCertificateEntry(t)).get())
                                                .map(t -> Try.of(() -> trustStore.getCertificate(t)).get())
                                                .toArray(X509Certificate[]::new))
                                        .andThen(certificates ->
                                                Try.of(() -> keyStore.getCertificateChain(keyAlias))
                                                        .andThen(certChain -> Try.of(() -> Arrays.stream(certChain)
                                                                .map(certificate -> (X509Certificate) certificate)
                                                                .collect(Collectors.toList())
                                                                .toArray(new X509Certificate[certChain.length]))
                                                                .andThen(x509CertificateChain -> Try.of(() -> sslContextSpec
                                                                                .sslContext(
                                                                                        SslContextBuilder.forClient()
                                                                                                .keyManager(
                                                                                                        Try.of(() -> (PrivateKey) keyStore.getKey(keyAlias, keyStorePass.toCharArray())).get(),
                                                                                                        keyStorePass,
                                                                                                        x509CertificateChain)
                                                                                                .trustManager(certificates)
                                                                                                .build()
                                                                                ).build()
                                                                        ).get()
                                                                ).get()
                                                        ).get()
                                        ).get())
                                .get()
                        ).get()
                ).get())
        ).get();
    }
}

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
import reactor.core.scheduler.Schedulers;
import reactor.netty.http.client.HttpClient;
import ru.msb.common.dao.ByteArrayContentDao;
import ru.msb.common.models.SSLStoreInfo;
import ru.msb.common.models.WebSocketClientInfo;
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
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
public class WebSocketService {

    private final ByteArrayContentDao byteArrayContentDao;
    private final ProjectSettings settings;

    @Autowired
    public WebSocketService(ByteArrayContentDao byteArrayContentDao, ProjectSettings settings) {
        this.byteArrayContentDao = byteArrayContentDao;
        this.settings = settings;
    }

    public void sendAndSubscribe(String requestName, byte[] sendingData) {
        final CountDownLatch latch = new CountDownLatch(1);
        final String threadName = Thread.currentThread().getName();
        Flux.merge(
                Flux.range(0, 1)
                        .subscribeOn(Schedulers.single())
                        .map(id -> sendAndSubscribe(requestName, threadName, sendingData))
                        .flatMap(sp -> sp.doOnTerminate(latch::countDown))
                        .parallel()
        )
                .subscribe();
        Try.of(() -> latch.await(20, TimeUnit.SECONDS)).get();
    }

    private Mono<Void> sendAndSubscribe(String requestName, String threadName, byte[] data) {
        final WebSocketClientInfo info = settings.getWss().get(requestName);
        final SSLStoreInfo ssl = info.getWss() ? settings.getSslStores().get(info.getSslStore()) : null;
        return (info.getWss() ?
                new ReactorNettyWebSocketClient(
                        getSecureClient(
                                ssl.getTruststoreLocation(),
                                ssl.getTruststorePassword(),
                                ssl.getKeystoreLocation(),
                                ssl.getKeystorePassword(),
                                requestName
                        ))
                : new ReactorNettyWebSocketClient())
                .execute(
                        URI.create(info.getUrl() + info.getMapping()),
                        session -> session.send(
                                Mono.just(session.binaryMessage(dataBufferFactory -> dataBufferFactory.wrap(data)))
                        )
                                .thenMany(session.receive()
                                        .take(Duration.ofSeconds(10L))
                                        .map(WebSocketMessage::retain)
                                        .doOnNext(dataBuffer -> {
                                            log.info("Получено сообщение: \n{}\nв потоке: {}",dataBuffer.getPayload().toString(Charset.defaultCharset()), threadName);
                                            byte[] bytes = new byte[dataBuffer.getPayload().readableByteCount()];
                                            dataBuffer.getPayload().read(bytes);
                                            DataBufferUtils.release(dataBuffer.getPayload());
                                            byteArrayContentDao.save(requestName, threadName, bytes);
                                        })
                                )
                                .doOnSubscribe(subscriber -> log.info(threadName + ".session create"))
                                .doFinally(signalType -> log.info(threadName + ".session close"))
                                .then());
    }

    public HttpClient getSecureClient(String trustStorePath, String trustStorePass, String keyStorePath, String keyStorePass, String keyAlias) {
        return Try.of(() ->
                HttpClient.create().secure(
                        sslContextSpec ->
                                Try.of(() ->
                                        Try.of(() -> KeyStore.getInstance(KeyStore.getDefaultType())
                                        ).andThen(trustStore ->
                                                Try.of(() ->
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
                                                        .andThen(keyStore ->
                                                                Try.of(() ->
                                                                        Collections.list(Try.of(trustStore::aliases).get())
                                                                                .stream()
                                                                                .filter(t -> Try.of(() -> trustStore.isCertificateEntry(t)
                                                                                ).get())
                                                                                .map(t -> Try.of(() -> trustStore.getCertificate(t)
                                                                                ).get()).toArray(X509Certificate[]::new))
                                                                        .andThen(certificates ->
                                                                                Try.of(() -> keyStore.getCertificateChain(keyAlias))
                                                                                        .andThen(certChain -> Try.of(() -> Arrays.stream(certChain)
                                                                                                .map(certificate -> (X509Certificate) certificate)
                                                                                                .collect(Collectors.toList())
                                                                                                .toArray(new X509Certificate[certChain.length]))
                                                                                                .andThen(x509CertificateChain ->
                                                                                                        Try.of(() -> sslContextSpec
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
                                                                        ).get()
                                                        ).get()
                                        ).get()
                                ).get()
                )
        ).get();
    }
}
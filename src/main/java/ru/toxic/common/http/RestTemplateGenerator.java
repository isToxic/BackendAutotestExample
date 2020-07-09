package ru.toxic.common.http;

import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.HttpClient;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.BasicHttpClientConnectionManager;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.SSLContexts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ru.toxic.common.enums.UsedTrustStrategy;
import ru.toxic.common.models.RestRequestInfo;
import ru.toxic.common.models.SSLStoreInfo;
import ru.toxic.common.setting.ProjectSettings;

import java.io.File;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.util.Map;

/**
 * Конфигурайия и генерация клиента для выполнения REST запросов
 */
@Slf4j
@Component
public class RestTemplateGenerator {

    private final ProjectSettings settings;

    @Autowired
    public RestTemplateGenerator(ProjectSettings settings) {
        this.settings = settings;
    }

    /**
     * Получение настроек коннекта
     *
     * @param requestName название коннекта
     * @return RestRequestInfo
     */
    public RestRequestInfo getRequestSettings(String requestName) {
        return settings.getRest().get(requestName);
    }

    /**
     * Конфигурация и генерация REST клиента по названию коннекта
     *
     * @param requestName название коннекта
     * @return RestTemplate
     */
    public RestTemplate takeTemplateFromSettings(String requestName) {
        RestRequestInfo info = settings.getRest().get(requestName);
        Map<String, SSLStoreInfo> sslStores = settings.getSslStores();
        switch (UsedTrustStrategy.valueOf(info.getTrustStrategy())) {
            case NOT_USED:
                log.debug("Начата конфигурация клиента для выполнения рест запросов без использования SSL");
                return getNoSSL();
            case ACCEPT_ALL:
                log.debug("Начата конфигурация клиента для выполнения рест запросов с использованием стратегии ACCEPT_ALL");
                return getAcceptAll();
            case TRUST_SELF_SIGNED:
                log.debug("Начата конфигурация клиента для выполнения рест запросов с использованием стратегии TRUST_SELF_SIGNED");
                return getKeyUserSSL(
                        sslStores.get(info.getSslStore()).getKeystoreLocation(),
                        sslStores.get(info.getSslStore()).getKeystorePassword());
            default:
                throw new IllegalArgumentException(String.format("Нет настроек для стратегии %s", info.getTrustStrategy()));
        }
    }

    private RestTemplate getNoSSL() {
        return new RestTemplate();
    }

    private RestTemplate getAcceptAll() {
        SSLConnectionSocketFactory socketFactory =
                Try.of(() -> new SSLConnectionSocketFactory(
                        Try.of(() -> SSLContexts.custom()
                                .loadTrustMaterial(null, (cert, authType) -> true)
                                .build()
                        ).get(),
                        NoopHostnameVerifier.INSTANCE)
                ).get();
        return new RestTemplate(
                new HttpComponentsClientHttpRequestFactory(
                        HttpClients.custom()
                                .setSSLSocketFactory(socketFactory)
                                .setConnectionManager(
                                        new BasicHttpClientConnectionManager(
                                                RegistryBuilder.<ConnectionSocketFactory>create()
                                                        .register("https", socketFactory)
                                                        .register("http", new PlainConnectionSocketFactory())
                                                        .build()
                                        )
                                ).build()
                )
        );
    }

    private RestTemplate getKeyUserSSL(
            @NonNull String keyStoreLocation,
            @NonNull String keyStorePassword) {
        return getTrustUserSSL(
                keyStoreLocation,
                keyStorePassword,
                null,
                null,
                null
        );
    }

    private RestTemplate getTrustUserSSL(
            @NonNull String keyStoreLocation,
            @NonNull String keyStorePassword,
            @Nullable String trustStoreLocation,
            @Nullable String trustStorePassword,
            @Nullable TrustStrategy trustStrategy) {
        return new RestTemplate(
                new HttpComponentsClientHttpRequestFactory(
                        getSSLHttClient(
                                keyStoreLocation,
                                keyStorePassword,
                                trustStoreLocation,
                                trustStorePassword,
                                trustStrategy))
        );
    }

    private HttpClient getSSLHttClient(
            @NonNull String keyStoreLocation,
            @NonNull String keyStorePassword,
            @Nullable String trustStoreLocation,
            @Nullable String trustStorePassword,
            @Nullable TrustStrategy trustStrategy) {
        return HttpClients.custom()
                .setSSLSocketFactory(new SSLConnectionSocketFactory(
                        trustStoreLocation != null && trustStorePassword != null && trustStrategy != null ?
                                Try.of(() -> new SSLContextBuilder()
                                        .loadTrustMaterial(
                                                new File(trustStoreLocation),
                                                trustStorePassword.toCharArray(),
                                                trustStrategy)
                                        .loadKeyMaterial(Try.of(() ->
                                                        KeyStore.getInstance(KeyStore.getDefaultType()))
                                                        .andThenTry(keyStore -> keyStore.load(
                                                                Try.of(() ->
                                                                        new FileInputStream(
                                                                                new File(keyStoreLocation))
                                                                ).get(),
                                                                keyStorePassword.toCharArray())
                                                        ).get(),
                                                keyStorePassword.toCharArray()).build()
                                ).get()
                                :
                                Try.of(() -> new SSLContextBuilder()
                                        .loadTrustMaterial(null, new TrustSelfSignedStrategy())
                                        .loadKeyMaterial(
                                                Try.of(() ->
                                                        KeyStore.getInstance(KeyStore.getDefaultType()))
                                                        .andThenTry(keyStore -> keyStore.load(
                                                                Try.of(() -> new FileInputStream(
                                                                        new File(keyStoreLocation))
                                                                ).get(),
                                                                keyStorePassword.toCharArray())
                                                        ).get(),
                                                keyStorePassword.toCharArray()).build()
                                ).get(),
                        NoopHostnameVerifier.INSTANCE)).build();
    }
}

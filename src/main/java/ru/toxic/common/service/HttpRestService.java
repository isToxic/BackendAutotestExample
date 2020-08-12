package ru.toxic.common.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.RequestEntity;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import ru.toxic.common.http.RestTemplateGenerator;
import ru.toxic.common.models.RestRequestInfo;
import ru.toxic.common.repository.ResponseEntityRepository;

import static ru.toxic.common.Common.genURI;

/**
 * Серис для выполнения REST запросов
 */
@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class HttpRestService {

    private final RestTemplateGenerator clientGenerator;
    private final ResponseEntityRepository repository;

    /**
     * Выполнение REST запроса методом PATCH
     *
     * @param requestName название коннекта
     * @param headers     отправляемые заголовки
     * @param body        тело запроса
     */
    public void doPATCH(String requestName, @Nullable MultiValueMap<String, String> headers, String body) {
        RestRequestInfo info = clientGenerator.getRequestSettings(requestName);
        repository.save(clientGenerator.takeTemplateFromSettings(requestName)
                        .exchange(RequestEntity
                                        .patch(genURI(
                                                info.getHost(),
                                                info.getMapping(),
                                                info.getPort())
                                        )
                                        .headers(httpHeaders -> {
                                            if (headers != null) {
                                                httpHeaders.addAll(headers);
                                            }
                                        })
                                        .body(body),
                                String.class),
                requestName);
    }

    /**
     * Выполнение REST запроса методом POST
     *
     * @param requestName название коннекта
     * @param headers     отправляемые заголовки
     * @param body        тело запроса
     */
    public void doPOST(String requestName, @Nullable MultiValueMap<String, String> headers, String body) {
        RestRequestInfo info = clientGenerator.getRequestSettings(requestName);
        repository.save(clientGenerator.takeTemplateFromSettings(requestName)
                        .exchange(RequestEntity
                                        .post(genURI(
                                                info.getHost(),
                                                info.getMapping(),
                                                info.getPort())
                                        )
                                        .headers(httpHeaders -> {
                                            if (headers != null) {
                                                httpHeaders.addAll(headers);
                                            }
                                        })
                                        .body(body),
                                String.class),
                requestName);
    }

    /**
     * Выполнение REST запроса методом PUT
     *
     * @param requestName название коннекта
     * @param headers     отправляемые заголовки
     * @param body        тело запроса
     */
    public void doPUT(String requestName, @Nullable MultiValueMap<String, String> headers, String body) {
        RestRequestInfo info = clientGenerator.getRequestSettings(requestName);
        repository.save(clientGenerator.takeTemplateFromSettings(requestName)
                        .exchange(RequestEntity
                                        .put(genURI(
                                                info.getHost(),
                                                info.getMapping(),
                                                info.getPort())
                                        )
                                        .headers(httpHeaders -> {
                                            if (headers != null) {
                                                httpHeaders.addAll(headers);
                                            }
                                        })
                                        .body(body),
                                String.class),
                requestName);
    }

    /**
     * Выполнение REST запроса методом PATCH
     *
     * @param requestName название коннекта
     * @param headers     отправляемые заголовки
     */
    public void doDELETE(String requestName, @Nullable MultiValueMap<String, String> headers) {
        RestRequestInfo info = clientGenerator.getRequestSettings(requestName);
        repository.save(clientGenerator.takeTemplateFromSettings(requestName)
                        .exchange(RequestEntity
                                        .delete(genURI(
                                                info.getHost(),
                                                info.getMapping(),
                                                info.getPort())
                                        )
                                        .headers(httpHeaders -> {
                                            if (headers != null) {
                                                httpHeaders.addAll(headers);
                                            }
                                        })
                                        .build(),
                                String.class),
                requestName);
    }

    /**
     * Выполнение REST запроса методом GET
     *
     * @param requestName название коннекта
     * @param headers     отправляемые заголовки
     */
    public void doGET(String requestName, @Nullable MultiValueMap<String, String> headers) {
        RestRequestInfo info = clientGenerator.getRequestSettings(requestName);
        repository.save(clientGenerator.takeTemplateFromSettings(requestName)
                        .exchange(RequestEntity
                                        .get(genURI(
                                                info.getHost(),
                                                info.getMapping(),
                                                info.getPort())
                                        )
                                        .headers(httpHeaders -> {
                                            if (headers != null) {
                                                httpHeaders.addAll(headers);
                                            }
                                        })
                                        .build(),
                                String.class),
                requestName);
    }

    /**
     * Выполнение REST запроса методом HEAD
     *
     * @param requestName название коннекта
     * @param headers     отправляемые заголовки
     */
    public void doHEAD(String requestName, @Nullable MultiValueMap<String, String> headers) {
        RestRequestInfo info = clientGenerator.getRequestSettings(requestName);
        repository.save(clientGenerator.takeTemplateFromSettings(requestName)
                        .exchange(RequestEntity
                                        .head(genURI(
                                                info.getHost(),
                                                info.getMapping(),
                                                info.getPort())
                                        )
                                        .headers(httpHeaders -> {
                                            if (headers != null) {
                                                httpHeaders.addAll(headers);
                                            }
                                        })
                                        .build(),
                                String.class),
                requestName);
    }

    /**
     * Выполнение REST запроса методом OPTIONS
     *
     * @param requestName название коннекта
     * @param headers     отправляемые заголовки
     */
    public void doOPTIONS(String requestName, @Nullable MultiValueMap<String, String> headers) {
        RestRequestInfo info = clientGenerator.getRequestSettings(requestName);
        repository.save(clientGenerator.takeTemplateFromSettings(requestName)
                        .exchange(RequestEntity
                                        .options(genURI(
                                                info.getHost(),
                                                info.getMapping(),
                                                info.getPort())
                                        )
                                        .headers(httpHeaders -> {
                                            if (headers != null) {
                                                httpHeaders.addAll(headers);
                                            }
                                        })
                                        .build(),
                                String.class),
                requestName);
    }
}

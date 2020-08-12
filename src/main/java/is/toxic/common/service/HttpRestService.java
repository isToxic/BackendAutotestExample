package is.toxic.common.service;

import is.toxic.common.Common;
import is.toxic.common.http.RestTemplateGenerator;
import is.toxic.common.models.RestRequestInfo;
import is.toxic.common.repository.ResponseEntityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.RequestEntity;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

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
                                        .patch(Common.genURI(
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
                                        .post(Common.genURI(
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
                                        .put(Common.genURI(
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
                                        .delete(Common.genURI(
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
                                        .get(Common.genURI(
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
                                        .head(Common.genURI(
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
                                        .options(Common.genURI(
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

package ru.msb.common.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.RequestEntity;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import ru.msb.common.repository.ResponseEntityRepository;
import ru.msb.common.http.RestTemplateGenerator;
import ru.msb.common.models.RestRequestInfo;

import static ru.msb.common.Common.genURI;

@Slf4j
@Service
public class HttpRestService {

    private final RestTemplateGenerator clientGenerator;
    private final ResponseEntityRepository repository;

    @Autowired
    public HttpRestService(RestTemplateGenerator clientGenerator, ResponseEntityRepository repository) {
        this.clientGenerator = clientGenerator;
        this.repository = repository;
    }

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

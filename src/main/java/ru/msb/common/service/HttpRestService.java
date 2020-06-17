package ru.msb.common.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.RequestEntity;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import ru.msb.common.dao.ResponseEntityDao;
import ru.msb.common.http.RestTemplateGenerator;
import ru.msb.common.models.RestRequestInfo;

import java.util.List;

import static ru.msb.common.Common.genURI;

@Slf4j
@Service
public class HttpRestService {

    private final RestTemplateGenerator templateGenerator;
    private final ResponseEntityDao dao;

    @Autowired
    public HttpRestService(RestTemplateGenerator templateGenerator, ResponseEntityDao dao) {
        this.templateGenerator = templateGenerator;
        this.dao = dao;
    }

    public void doPATCH(String requestName, @Nullable MultiValueMap<String, String> headers, String body) {
        RestRequestInfo info = templateGenerator.getRequestSettings(requestName);
        dao.save(templateGenerator.takeTemplateFromSettings(requestName)
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
        RestRequestInfo info = templateGenerator.getRequestSettings(requestName);
        dao.save(templateGenerator.takeTemplateFromSettings(requestName)
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
        RestRequestInfo info = templateGenerator.getRequestSettings(requestName);
        dao.save(templateGenerator.takeTemplateFromSettings(requestName)
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
        RestRequestInfo info = templateGenerator.getRequestSettings(requestName);
        dao.save(templateGenerator.takeTemplateFromSettings(requestName)
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
        RestRequestInfo info = templateGenerator.getRequestSettings(requestName);
        dao.save(templateGenerator.takeTemplateFromSettings(requestName)
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
        RestRequestInfo info = templateGenerator.getRequestSettings(requestName);
        dao.save(templateGenerator.takeTemplateFromSettings(requestName)
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
        RestRequestInfo info = templateGenerator.getRequestSettings(requestName);
        dao.save(templateGenerator.takeTemplateFromSettings(requestName)
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

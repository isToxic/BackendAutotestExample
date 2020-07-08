package ru.msb.stepdefs;

import io.cucumber.datatable.DataTable;
import io.cucumber.java8.Ru;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.LinkedMultiValueMap;
import ru.msb.common.repository.TestCache;
import ru.msb.common.service.HttpRestService;

import java.util.List;

import static ru.msb.common.Common.REST_STRING_REQUEST_MESSAGE;
import static ru.msb.common.Common.generateStringKey;

public class HttpRestTransportSteps implements Ru {

    @Autowired
    private TestCache storage;

    @Autowired
    private HttpRestService service;

    public HttpRestTransportSteps() {
        Допустим("выполняю запрос {string} методом GET без заголовков", (String requestName) -> service.doGET( requestName, null)
        );
        Допустим("выполняю запрос {string} методом GET используя заголовки:", (String requestName, DataTable headers) ->
                service.doGET(requestName, new LinkedMultiValueMap<String, String>(headers.asMap(String.class, List.class)))
        );
        Допустим("выполняю запрос {string} методом HEAD без заголовков", (String requestName) -> service.doHEAD(requestName, null)
        );
        Допустим("выполняю запрос {string} методом HEAD используя заголовки:", (String requestName, DataTable headers) ->
                service.doHEAD(requestName, new LinkedMultiValueMap<String, String>(headers.asMap(String.class, List.class)))
        );
        Допустим("выполняю запрос {string} методом DELETE без заголовков", (String requestName) -> service.doDELETE(requestName, null)
        );
        Допустим("выполняю запрос {string} методом DELETE используя заголовки:", (String requestName, DataTable headers) ->
                service.doDELETE(requestName, new LinkedMultiValueMap<String, String>(headers.asMap(String.class, List.class)))
        );
        Допустим("выполняю запрос {string} методом OPTIONS без заголовков", (String requestName) -> service.doOPTIONS(requestName, null)
        );
        Допустим("выполняю запрос {string} методом OPTIONS используя заголовки:", (String requestName, DataTable headers) ->
                service.doOPTIONS(requestName, new LinkedMultiValueMap<String, String>(headers.asMap(String.class, List.class)))
        );
        Допустим("выполняю запрос {string} методом POST без заголовков", (String requestName) ->
                service.doPOST(requestName, null, storage.getString(generateStringKey(REST_STRING_REQUEST_MESSAGE)))
        );
        Допустим("выполняю запрос {string} методом POST используя заголовки:", (String requestName, DataTable headers) ->
                service.doPOST(
                        requestName,
                        new LinkedMultiValueMap<String, String>(headers.asMap(String.class, List.class)),
                        storage.getString(generateStringKey(REST_STRING_REQUEST_MESSAGE))
                )
        );
        Допустим("выполняю запрос {string} методом PATCH без заголовков", (String requestName) ->
                service.doPATCH(requestName, null, storage.getString(generateStringKey(REST_STRING_REQUEST_MESSAGE)))
        );
        Допустим("выполняю запрос {string} методом PATCH используя заголовки:", (String requestName, DataTable headers) ->
                service.doPATCH(
                        requestName,
                        new LinkedMultiValueMap<String, String>(headers.asMap(String.class, List.class)),
                        storage.getString(generateStringKey(REST_STRING_REQUEST_MESSAGE))
                )
        );
        Допустим("выполняю запрос {string} методом PUT без заголовков", (String requestName) ->
                service.doPUT(requestName, null, storage.getString(generateStringKey(REST_STRING_REQUEST_MESSAGE)))
        );
        Допустим("выполняю запрос {string} методом PUT используя заголовки:", (String requestName, DataTable headers) ->
                service.doPUT(
                        requestName,
                        new LinkedMultiValueMap<String, String>(headers.asMap(String.class, List.class)),
                        storage.getString(generateStringKey(REST_STRING_REQUEST_MESSAGE))
                )
        );
    }
}

package ru.toxic.stepdefs;

import io.cucumber.java8.Ru;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import ru.toxic.common.repository.TestCache;
import ru.toxic.common.service.WebSocketService;

import static ru.toxic.common.Common.WS_STRING_REQUEST_MESSAGE;
import static ru.toxic.common.Common.generateStringKey;


@Slf4j
public class WebSocketTransportSteps implements Ru {

    @Autowired
    private WebSocketService webSocketService;

    @Autowired
    private TestCache storage;

    public WebSocketTransportSteps() {
        Допустим("отправляю по ws соединению {string} подготовленное сообщение и слушаю канал {int} секунду/секунд/секунды", (String reqestName, Integer timeout) ->
                webSocketService.sendAndSubscribe(reqestName, storage.getString(generateStringKey(WS_STRING_REQUEST_MESSAGE)).getBytes(), timeout)
        );
    }
}

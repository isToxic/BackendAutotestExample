package ru.msb.stepdefs;

import io.cucumber.java8.Ru;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import ru.msb.common.repository.TestCache;
import ru.msb.common.service.WebSocketService;

import static ru.msb.common.Common.WS_STRING_REQUEST_MESSAGE;
import static ru.msb.common.Common.generateStringKey;


@Slf4j
public class WebSocketTransportSteps implements Ru {

    @Autowired
    private WebSocketService webSocketService;

    @Autowired
    private TestCache storage;

    public WebSocketTransportSteps() {
        Допустим("отправляю по ws соединению {string} подготовленное сообщение и слушаю канал {int} секунд", (String reqestName, Integer timeout) ->
                webSocketService.sendAndSubscribe(reqestName, storage.getString(generateStringKey(WS_STRING_REQUEST_MESSAGE)).getBytes(), timeout)
        );
    }
}

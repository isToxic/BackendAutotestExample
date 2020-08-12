package is.toxic.stepdefs;

import io.cucumber.java8.Ru;
import is.toxic.common.Common;
import is.toxic.common.repository.TestCache;
import is.toxic.common.service.WebSocketService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public class WebSocketTransportSteps implements Ru {

    @Autowired
    private WebSocketService webSocketService;

    @Autowired
    private TestCache storage;

    public WebSocketTransportSteps() {
        Допустим("отправляю по ws соединению {string} подготовленное сообщение и слушаю канал {int} секунду/секунд/секунды", (String reqestName, Integer timeout) ->
                webSocketService.sendAndSubscribe(reqestName, storage.getString(Common.generateStringKey(Common.WS_STRING_REQUEST_MESSAGE)).getBytes(), timeout)
        );
    }
}

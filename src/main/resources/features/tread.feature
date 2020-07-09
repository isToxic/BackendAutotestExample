#language: ru

  @Treads
  Функциональность:  Проверка

    Сценарий: REST
      Допустим генерирую сообщение по схеме "testSchema1.json" для REST запроса
      Тогда выполняю запрос "reqres" методом POST без заголовков
      И проверяю, что ответ на REST запрос "reqres" получен с успешным статус кодом
      И проверяю, что ответ на REST запрос "reqres" соответствует "resp.json" схеме

    Сценарий: REST
      Допустим генерирую сообщение по схеме "testSchema1.json" для REST запроса
      Допустим выполняю запрос "reqres" методом POST используя заголовки:
      |Connection|keep-alive|
      И проверяю, что ответ на REST запрос "reqres" получен с успешным статус кодом
      И проверяю, что ответ на REST запрос "reqres" соответствует "resp.json" схеме

      Структура сценария:  WebSocket <название очереди>
        Допустим генерирую сообщение по схеме <схема генерации> для запроса ws
        Тогда отправляю по ws соединению <имя коннекта> подготовленное сообщение и слушаю канал 2 секунды
        И проверяю, что ответ на ws запрос <имя коннекта> соответствует <схема проверки> схеме

        Примеры:
          | схема генерации    | имя коннекта | схема проверки     | название очереди |
          | "testSchema1.json" | "test"                | "testSchema1.json" | 1              |
          | "testSchema2.json" | "test"                | "testSchema2.json" | 2              |
          | "testSchema3.json" | "test"                | "testSchema3.json" | 3              |
          | "testSchema4.json" | "test"                | "testSchema4.json" | 4              |
          | "testSchema5.json" | "test"                | "testSchema5.json" | 5              |
          | "testSchema6.json" | "test"                | "testSchema6.json" | 6              |
          | "testSchema7.json" | "test"                | "testSchema7.json" | 7              |

    Структура сценария: Запись и вычитка сообщений Kafka <префикс теста>
      Допустим генерирую сообщение по схеме <схема генерации> для запроса Kafka
      Тогда отправляю сообщение в очередь <название очереди>, брокера <имя брокера>
      И вычитываю сообщение из очереди <название очереди> брокера <имя брокера> содержащее <параметр поиска>
      И проверяю, что ответ из очереди <название очереди> соответствует <схема проверки> схеме

      Примеры:
        | схема генерации    | имя брокера | схема проверки      | название очереди | параметр поиска  | префикс теста |
        | "testSchema1.json" | "broker1"         | "testSchema1.json" | "TutorialTopic"         | "BLACK"                  | 1                         |
        | "testSchema2.json" | "broker1"         | "testSchema2.json" | "TutorialTopic"         | "GREEN"                 | 2                         |
        | "testSchema3.json" | "broker1"         | "testSchema3.json" | "TutorialTopic"         | "WHITE"                  | 3                         |
        | "testSchema4.json" | "broker1"         | "testSchema4.json" | "TutorialTopic"         | "BLUE"                    | 4                         |
        | "testSchema5.json" | "broker1"         | "testSchema5.json" | "TutorialTopic"         | "GREY"                   | 5                          |
        | "testSchema6.json" | "broker1"         | "testSchema6.json" | "TutorialTopic"         | "YELLOW"              | 6                          |
        | "testSchema7.json" | "broker1"         | "testSchema7.json" | "TutorialTopic"         | "RED"                     | 7                          |

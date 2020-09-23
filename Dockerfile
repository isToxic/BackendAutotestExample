FROM gradle:jdk11
WORKDIR /usr/autotest
ENTRYPOINT git clone https://github.com/isToxic/BackendAutotestExample.git && cd BackendAutotestExample && gradle cucumber
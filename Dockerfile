FROM maven:3.8.5-openjdk-17-slim

ADD . /usr/src/votacao_backend
WORKDIR /usr/src/votacao_backend
EXPOSE 8081
ENTRYPOINT ["mvn", "clean", "install", "spring-boot:run"]

FROM maven:3.8.5-openjdk-17-slim

ADD . /usr/src/backend_module
WORKDIR /usr/src/backend_module
EXPOSE 8081
ENTRYPOINT ["mvn", "clean", "install", "spring-boot:run"]

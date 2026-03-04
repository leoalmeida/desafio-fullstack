# Create image based on the official openjdk image from dockerhub
# BUILD
FROM maven:3.8.5-openjdk-17-slim AS build
COPY . /usr/src/backend_module
WORKDIR /usr/src/backend_module
CMD ["mvn", "clean", "package", "-DskipTests"]


# RUN
FROM eclipse-temurin:17-jdk-alpine
COPY --from=build /usr/src/backend_module/target/backend_module*.jar /usr/src/backend_module/app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "/usr/src/backend_module/app.jar"]
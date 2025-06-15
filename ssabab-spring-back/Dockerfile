FROM openjdk:17

WORKDIR ./app

COPY ./build/libs/ssabab-spring-0.0.1-SNAPSHOT.jar ./app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
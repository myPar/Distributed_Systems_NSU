FROM eclipse-temurin:17-jdk-alpine
VOLUME /tmp
ARG JAR_FILE
COPY ${JAR_FILE} worker.jar
ENTRYPOINT ["java","-jar","/worker.jar"]
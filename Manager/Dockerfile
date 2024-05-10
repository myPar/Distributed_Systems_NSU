FROM eclipse-temurin:17-jdk-alpine
VOLUME /tmp
ARG JAR_FILE
COPY ${JAR_FILE} manager.jar
ENTRYPOINT ["java","-jar","/manager.jar"]
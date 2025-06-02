FROM openjdk:23-jdk
ARG JAR_FILE=target/GameManagement-0.0.1.jar
COPY ${JAR_FILE} app_gameManagement.jar
EXPOSE 8003
ENTRYPOINT ["java","-jar","app_gameManagement.jar"]
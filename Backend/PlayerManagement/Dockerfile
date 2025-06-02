FROM openjdk:23-jdk
ARG JAR_FILE=target/PlayerManagment-0.0.1.jar
COPY ${JAR_FILE} app_playerManagement.jar
EXPOSE 8001
ENTRYPOINT ["java","-jar","app_playerManagement.jar"]
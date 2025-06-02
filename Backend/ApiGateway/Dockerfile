FROM openjdk:23-jdk
ARG JAR_FILE=target/ApiGateway-0.0.1.jar
COPY ${JAR_FILE} app_apiGateway.jar
EXPOSE 8004
ENTRYPOINT ["java","-jar","app_apiGateway.jar"]
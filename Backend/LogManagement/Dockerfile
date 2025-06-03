FROM maven as mv
RUN apt-get update && apt install -y git
RUN git clone https://github.com/sebastian11020/Monopoly.git
WORKDIR /Monopoly/Backend/ApiGateway
RUN mvn clean install -DskipTests

FROM openjdk:23-jdk
ARG JAR_FILE=target/LogManagement-0.0.1.jar
COPY ${JAR_FILE} app_logManagement.jar
EXPOSE 8004
ENTRYPOINT ["java","-jar","app_logManagement.jar"]
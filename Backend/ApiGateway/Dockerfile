FROM maven:3.9.0 as mv
RUN apt-get update && apt install -y git
RUN git clone https://github.com/sebastian11020/Monopoly.git
WORKDIR /Monopoly/Backend/ApiGateway
RUN mvn clean install -DskipTests

FROM openjdk:21-jdk
WORKDIR /ApiGateway
COPY --from=mv /Monopoly/Backend/ApiGateway/target/*.jar /ApiGateway/app_apiGateway.jar
EXPOSE 8004
ENTRYPOINT ["java","-jar","app_apiGateway.jar"]

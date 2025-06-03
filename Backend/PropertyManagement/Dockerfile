FROM maven:3.9.0-sapmachine-21 as mv
RUN apt-get update && apt install -y git
RUN git clone https://github.com/sebastian11020/Monopoly.git
WORKDIR /Monopoly/Backend/PropertyManagement
RUN mvn clean install -DskipTests

FROM openjdk:21-jdk
WORKDIR /PropertyManagement
COPY --from=mv /Monopoly/Backend/PropertyManagement/target/*.jar /PropertyManagement/app_apiGateway.jar
ENTRYPOINT ["java","-jar","app_propertyManagement.jar"]

FROM maven:3.9.0-sapmachine-17 as mv
RUN apt-get update && apt install -y git
RUN git clone https://github.com/sebastian11020/Monopoly.git
WORKDIR /Monopoly/Backend/PlayerManagement
RUN mvn clean install -DskipTests

FROM openjdk:23-jdk
WORKDIR /PlayerManagement
COPY --from=mv /Monopoly/Backend/PlayerManagement/target/*.jar /PlayerManagement/app_playerManagment.jar
ENTRYPOINT ["java","-jar","app_playerManagment.jar"]

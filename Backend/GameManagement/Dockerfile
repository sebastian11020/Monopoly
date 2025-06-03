FROM maven:3.9.0-sapmachine-21 as mv
RUN apt-get update && apt install -y git
RUN git clone https://github.com/sebastian11020/Monopoly.git
WORKDIR /Monopoly/Backend/GameManagement
RUN mvn clean install -DskipTests

FROM openjdk:21-jdk
WORKDIR /GameManagement
COPY --from=mv /Monopoly/Backend/GameManagement/target/*.jar /GameManagement/app_gameManagement.jar
ENTRYPOINT ["java","-jar","app_gameManagement.jar"]

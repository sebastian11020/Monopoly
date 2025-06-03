FROM maven:3.9.0 as mv
RUN apt-get update && apt install -y git
RUN git clone https://github.com/sebastian11020/Monopoly.git
WORKDIR /Monopoly/Backend/Eureka
RUN mvn clean install -DskipTests

FROM openjdk:23-jdk
WORKDIR /Eureka
COPY --from=mv /Monopoly/Backend/Eureka/target/*.jar /Eureka/app_eureka.jar
EXPOSE 8761
ENTRYPOINT ["java","-jar","app_eureka.jar"]

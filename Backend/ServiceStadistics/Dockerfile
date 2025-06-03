FROM openjdk:23-jdk
ARG JAR_FILE=target/ServiceStatistics-0.0.1.jar
COPY ${JAR_FILE} app_serviceStatistics.jar
EXPOSE 8004
ENTRYPOINT ["java","-jar","app_serviceStatistics.jar"]
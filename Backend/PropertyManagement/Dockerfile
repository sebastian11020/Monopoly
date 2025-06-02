FROM openjdk:23-jdk
ARG JAR_FILE=target/PropertyManagement-0.0.1.jar
COPY ${JAR_FILE} app_propertyManagement.jar
EXPOSE 8002
ENTRYPOINT ["java","-jar","app_propertyManagement.jar"]
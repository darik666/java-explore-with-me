FROM amazoncorretto:11-alpine-jdk
COPY target/*.jar ewm-service.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/ewm-service.jar"]
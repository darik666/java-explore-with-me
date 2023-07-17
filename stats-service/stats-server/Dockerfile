FROM amazoncorretto:11-alpine-jdk
COPY target/*.jar stats-server.jar
EXPOSE 9090
ENTRYPOINT ["java","-jar","/stats-server.jar"]

FROM amazoncorretto:11-alpine-jdk
LABEL maintainer="AnnaZvereva"
COPY target/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
EXPOSE 8080
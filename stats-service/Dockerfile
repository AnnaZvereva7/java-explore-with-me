FROM amazoncorretto:11-alpine-jdk
LABEL maintainer="AnnaZvereva"
COPY service-part/target/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
EXPOSE 9090
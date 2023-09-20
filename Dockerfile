FROM openjdk:17-jdk-alpine

WORKDIR /app

COPY target/*.war /app/app.war

ENTRYPOINT ["java","-jar","/app/app.war"]

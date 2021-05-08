FROM openjdk:11-jre-slim
COPY target/weather-1.0-SNAPSHOT.jar /weather.jar
EXPOSE 8082
ENTRYPOINT ["java","-jar","/weather.jar"]
FROM openjdk:11-jre-slim
COPY target/prediction-1.0-SNAPSHOT.jar /prediction.jar
EXPOSE 8083
ENTRYPOINT ["java","-jar","/prediction.jar"]

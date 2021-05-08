FROM openjdk:11-jre-slim
COPY target/currency-1.0-SNAPSHOT.jar /currency.jar
EXPOSE 8081
ENTRYPOINT ["java","-jar","/currency.jar"]
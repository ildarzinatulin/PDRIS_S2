FROM openjdk:11-jre-slim
COPY target/eureka-1.0-SNAPSHOT.jar /eureka.jar
EXPOSE 8761
ENTRYPOINT ["java","-jar","/eureka.jar"]
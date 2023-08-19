FROM openjdk:17-oracle
COPY target/authentication-service-0.0.1-SNAPSHOT.jar authentication-service.jar
ENTRYPOINT ["java","-jar","/authentication-service.jar"]
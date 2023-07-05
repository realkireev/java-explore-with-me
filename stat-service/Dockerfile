FROM amazoncorretto:11-alpine-jdk
COPY stat-service-client/target/*.jar stat-service-client-0.0.1-SNAPSHOT.jar
COPY stat-service-dto/target/*.jar stat-service-dto-0.0.1-SNAPSHOT.jar
COPY stat-service-server/target/*.jar stat-service-server-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java","-jar","/stat-service-server-0.0.1-SNAPSHOT.jar"]

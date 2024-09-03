FROM maven:3.8.4-openjdk-17 AS builder
WORKDIR /app
COPY pom.xml /app/
RUN mvn dependency:go-offline -B
COPY src /app/src
RUN mvn clean package -DskipTests

FROM openjdk:17-oracle
WORKDIR /app
COPY --from=builder /app/target/*.jar /app/application.jar
EXPOSE 13401
ENTRYPOINT ["java", "-jar", "/app/application.jar"]
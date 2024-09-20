FROM maven:3.8.4-openjdk-17 as builder

WORKDIR /app

ADD pom.xml .

RUN mvn verify --fail-never

ADD . .

RUN mvn clean package -Dmaven.test.skip=true

FROM openjdk:17-jdk-slim

WORKDIR /app

COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]

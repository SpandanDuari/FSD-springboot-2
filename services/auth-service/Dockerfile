FROM maven:3.9-eclipse-temurin-25 AS build
WORKDIR /app
COPY pom.xml .
COPY src src
RUN mvn -q -DskipTests package
FROM eclipse-temurin:25-jre
COPY --from=build /app/target/auth-service-1.0.0.jar /app.jar
ENTRYPOINT ["java","-jar","/app.jar"]

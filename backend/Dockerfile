FROM maven:3.9.6-eclipse-temurin-21-jammy AS build
WORKDIR /workspace

COPY backend/pom.xml .
COPY backend/src ./src

RUN mvn clean package -DskipTests


FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

COPY --from=build /workspace/target/quarkus-app/ /app/quarkus-app/

EXPOSE 8081

CMD ["java", "-jar", "/app/quarkus-app/quarkus-run.jar"]
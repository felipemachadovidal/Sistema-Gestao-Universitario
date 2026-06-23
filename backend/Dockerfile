FROM maven:3.9.6-eclipse-temurin-21-jammy AS build
WORKDIR /workspace

COPY backend/pom.xml .
COPY backend/src ./src

# Executa o build gerando o pacote
RUN mvn clean package -DskipTests

# Estágio 2: Runtime leve
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# Copia os artefatos gerados direto da pasta do workspace do build
COPY --from=build /workspace/target/quarkus-app/ /app/quarkus-app/

EXPOSE 8081

CMD ["java", "-jar", "/app/quarkus-app/quarkus-run.jar"]
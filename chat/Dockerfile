# ==== Stage 1: Build the application ====
FROM maven:3.8.3-openjdk-17 AS builder

# Setează directorul de lucru
WORKDIR /app

# Copiază fișierele proiectului
COPY pom.xml .
COPY src ./src

# Compilează aplicația Java și generează fișierul JAR
RUN mvn clean package -DskipTests

# ==== Stage 2: Runtime ====
FROM openjdk:17-jdk-slim

# Setează directorul de lucru pentru runtime
WORKDIR /app

# Copiază fișierul JAR generat din etapa de build
COPY --from=builder /app/target/chat-0.0.1-SNAPSHOT.jar app.jar

# Expune portul aplicației
EXPOSE 8085

# Rulează aplicația Java
ENTRYPOINT ["java", "-jar", "app.jar"]

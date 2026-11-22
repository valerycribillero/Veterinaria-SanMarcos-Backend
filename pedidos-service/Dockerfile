# Compila el microservicio dentro de Docker.
FROM maven:3.9-eclipse-temurin-21 AS compilacion
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn -B -DskipTests package

# Ejecuta el archivo generado con Java 21.
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=compilacion /app/target/*-exec.jar app.jar
EXPOSE 8087
ENTRYPOINT ["java", "-jar", "app.jar"]

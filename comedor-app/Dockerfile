# Usamos la imagen de Maven para compilar el proyecto
FROM maven:3.8.5-openjdk-17 AS build

WORKDIR /app

COPY pom.xml .
COPY src ./src

# Construimos el proyecto y empaquetamos el JAR
RUN mvn clean package -DskipTests

# Imagen para ejecutar la aplicación
FROM openjdk:17-jdk-slim

WORKDIR /app

# Copiamos el JAR generado de la etapa de build
COPY --from=build /app/target/*.jar app.jar

# Puerto que expone la app
EXPOSE 8080

# Comando para ejecutar el JAR
ENTRYPOINT ["java", "-jar", "app.jar"]

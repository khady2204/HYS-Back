# Étape de build
FROM maven:3.8.7-eclipse-temurin-17 AS build
WORKDIR /app

# Copie des fichiers de configuration et du code source
COPY pom.xml .
COPY src ./src

# Construction de l'application
RUN mvn clean package -DskipTests

# Étape d'exécution
FROM eclipse-temurin:17-jre
WORKDIR /app

# Copie du JAR construit
COPY --from=build /app/target/*.jar app.jar

# Création du dossier pour les uploads
RUN mkdir -p /app/uploads

# Exposition du port de l'application
EXPOSE 8081

# Commande de démarrage
ENTRYPOINT ["java", "-jar", "app.jar"]

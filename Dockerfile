# Etapa 1: build do projeto
FROM maven:3.9.6-eclipse-temurin-21 AS builder
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests
  
# Etapa 2: imagem final mais leve
FROM eclipse-temurin:21-jdk
WORKDIR /app
  
# Copia o jar gerado na etapa anterior
COPY --from=builder /app/target/*.jar app.jar
  
# Expõe a porta (mesma configurada no docker-compose.yml)
EXPOSE 8087

# Usa variável de ambiente para facilitar troca de porta se precisar
ENTRYPOINT ["java", "-jar", "app.jar"]
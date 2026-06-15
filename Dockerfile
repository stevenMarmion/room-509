FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY ./target/*.jar manage-your-little-fish.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "manage-your-little-fish.jar"]

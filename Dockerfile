FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY ./manage-your-little-fish/target/*.jar manage-your-little-fish.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "manage-your-little-fish.jar"]

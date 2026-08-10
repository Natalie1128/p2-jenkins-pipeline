# Build stage
FROM maven:3.8-openjdk-17 AS build
WORKDIR /manager_app
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn clean package -DskipTests

# Runtime stage
FROM maven:3.8-openjdk-17
WORKDIR /manager_app
COPY --from=build /manager_app/target/*.jar app.jar
EXPOSE 9090
ENTRYPOINT ["java", "-jar", "app.jar"]

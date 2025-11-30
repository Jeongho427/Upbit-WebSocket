FROM eclipse-temurin:17-jdk-jammy

WORKDIR /app

COPY build/libs/websocket-test-0.0.1-SNAPSHOT.jar app.jar

# 프로필은 Dockerfile에서 안 박고, compose에서만 제어
# ENV SPRING_PROFILES_ACTIVE=prod

EXPOSE 8080

ENTRYPOINT ["java","-jar","app.jar"]

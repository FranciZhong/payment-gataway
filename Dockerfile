FROM openjdk
LABEL authors="francis"
WORKDIR /app
COPY build/libs/payment-gateway-0.0.1-SNAPSHOT.jar app.jar
CMD ["java", "-jar", "app.jar"]
FROM amazoncorretto:21-alpine
WORKDIR /app

COPY target/stock-simulator.jar .

EXPOSE 8081

ENTRYPOINT ["java","-jar","stock-simulator.jar"]

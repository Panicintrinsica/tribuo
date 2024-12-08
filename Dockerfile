FROM openjdk:17-jdk-alpine
WORKDIR /app
COPY target/tribuo-1.0.jar /app/tribuo.jar
RUN mkdir -p /app/log
CMD ["sh", "-c", "java -jar /app/tribuo.jar > /app/log/output.log 2>&1"]
EXPOSE 8008
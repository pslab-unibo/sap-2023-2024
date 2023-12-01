# 1st Docker build stage: build the project with Gradle
FROM gradle:7.2.0-jdk17 as builder
WORKDIR /project
COPY . /project/
RUN gradle assemble --no-daemon

# 2nd Docker build stage: copy builder output and configure entry point
FROM openjdk:17
ENV APP_DIR /application
ENV APP_FILE cooperative-pixelart-service-jar.jar

EXPOSE 9000

WORKDIR $APP_DIR
COPY --from=builder /project/app/build/libs/*.jar $APP_DIR/$APP_FILE

ENTRYPOINT ["sh", "-c"]
CMD ["exec java -jar $APP_FILE"]


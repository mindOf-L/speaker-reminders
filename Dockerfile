#maven
FROM maven:3.9.11-eclipse-temurin-25-alpine AS maven
LABEL MAINTAINER="mindOf_L"

WORKDIR /build
COPY . /build
RUN mvn package

#java
FROM eclipse-temurin:25-jdk-ubi10-minimal AS backend
ENV JAVA_OPTS "-XX:MaxRAMPercentage=70 -Djava.security.egd=file:/dev/./urandom"
ARG JAR_FILE=speaker-reminder.jar

ENV APP_HOME /opt/app
WORKDIR $APP_HOME

# Copy the backend jar from the maven stage to the /opt/app directory of the current stage.
COPY --from=maven /build/target/${JAR_FILE} $APP_HOME

EXPOSE 7877

ENTRYPOINT exec java $JAVA_OPTS -jar $APP_HOME/speaker-reminder.jar

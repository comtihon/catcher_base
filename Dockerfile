FROM gradle:7-jdk11-alpine AS BUILD_IMAGE
ENV APP_HOME=/usr/app/
WORKDIR $APP_HOME

RUN apk add --update --no-cache python3 && ln -sf python3 /usr/bin/python
RUN apk add --update npm
RUN npm install -g @angular/cli

COPY build.gradle settings.gradle $APP_HOME

COPY gradle $APP_HOME/gradle
COPY . .

RUN gradle clean build || return 0

# ======================================================================================================================
FROM openjdk:11-alpine
ENV ARTIFACT_NAME=pokerstats-0.0.1-SNAPSHOT.jar
ENV APP_HOME=/usr/app/

WORKDIR $APP_HOME
COPY --from=BUILD_IMAGE $APP_HOME/build/libs/$ARTIFACT_NAME .

# TODO include catcher
# TODO include catcher-modules and all module deps.

EXPOSE 8080
ENTRYPOINT exec java -jar ${ARTIFACT_NAME}
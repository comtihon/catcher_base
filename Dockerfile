FROM gradle:7-jdk11-alpine AS BUILD_IMAGE
ENV APP_HOME=/usr/app/
ENV SHELL=/bin/sh
WORKDIR $APP_HOME

RUN apk add --update --no-cache python3 py-pip && ln -sf python3 /usr/bin/python
RUN apk add --update npm
RUN npm install -g @angular/cli

COPY build.gradle settings.gradle $APP_HOME

COPY gradle $APP_HOME/gradle
COPY . .

RUN gradle clean build

# ======================================================================================================================
FROM comtihon/catcher
ENV ARTIFACT_NAME=base-0.0.1-SNAPSHOT.jar
ENV APP_HOME=/usr/app/
ENV SHELL=/bin/sh

# to resolve postgres host if both containers running locally
RUN apk add bind-tools

# TODO nodejs and angular?

WORKDIR $APP_HOME
COPY res/application.yml $APP_HOME/config/
COPY --from=BUILD_IMAGE $APP_HOME/build/libs/$ARTIFACT_NAME .

EXPOSE 8080
ENTRYPOINT exec java -jar ${ARTIFACT_NAME} --spring.config.location=file://$APP_HOME/config/
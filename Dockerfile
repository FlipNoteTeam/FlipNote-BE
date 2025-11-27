FROM gradle:8.14-jdk17 AS build
WORKDIR /app

COPY build.gradle settings.gradle ./
COPY src ./src

RUN gradle bootJar --no-daemon

FROM amazoncorretto:17.0.17-alpine3.22
WORKDIR /app

ENV TZ=Asia/Seoul
RUN apk add --no-cache tzdata \
    && ln -sf /usr/share/zoneinfo/$TZ /etc/localtime \
    && echo $TZ > /etc/timezone

COPY --from=build /app/build/libs/flipnote-0.0.1-SNAPSHOT.jar .

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "flipnote-0.0.1-SNAPSHOT.jar"]

FROM --platform=$BUILDPLATFORM maven:3.9.6-eclipse-temurin-21 as builder

WORKDIR /app

COPY . .

RUN mvn clean package

FROM openlink/virtuoso-opensource-7:7.2.12-r18-gb5ac0bd-ubuntu as main

RUN mkdir /exp /exp/store /exp/data /exp/result

ENV JAVA_HOME=/opt/java/openjdk
COPY --from=docker.io/eclipse-temurin:21 $JAVA_HOME $JAVA_HOME
ENV PATH="${JAVA_HOME}/bin:${PATH}"

WORKDIR /app
COPY --from=builder /root/build /app
COPY ./config /app/config

ENTRYPOINT []

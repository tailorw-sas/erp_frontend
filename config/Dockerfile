FROM ghcr.io/graalvm/native-image:ol8-java17-22 AS builder

# Install tar and gzip to extract the Maven binaries
RUN microdnf update \
    && microdnf install --nodocs \
    tar \
    gzip \
    && microdnf clean all \
    && rm -rf /var/cache/yum

# Install Maven
# Source:
# 1) https://github.com/carlossg/docker-maven/blob/925e49a1d0986070208e3c06a11c41f8f2cada82/openjdk-17/Dockerfile
# 2) https://maven.apache.org/download.cgi
ARG USER_HOME_DIR="/root"
ARG MAVEN_VERSION=3.9.4
ARG MAVEN_DOWNLOAD_URL=https://archive.apache.org/dist/maven/maven-3/${MAVEN_VERSION}/binaries/apache-maven-${MAVEN_VERSION}-bin.tar.gz

RUN mkdir -p /usr/share/maven /usr/share/maven/ref \
    && curl -fsSL -o /tmp/apache-maven.tar.gz ${MAVEN_DOWNLOAD_URL} \
    && tar -xzf /tmp/apache-maven.tar.gz -C /usr/share/maven --strip-components=1 \
    && rm -f /tmp/apache-maven.tar.gz \
    && ln -s /usr/share/maven/bin/mvn /usr/bin/mvn

ENV MAVEN_HOME /usr/share/maven/bin/
ENV MAVEN_CONFIG "$USER_HOME_DIR/.m2"

# Set the working directory to /home/app
WORKDIR /build

# Copy the source code into the image for building
COPY . /build

#RUN rm -rvf target

# Setup environment vairables for maven resposiory
ARG PACKAGE_TOKEN
ENV PACKAGE_TOKEN ${PACKAGE_TOKEN}

# Build
RUN mvn dependency:go-offline -f pom.xml -s rc-settings.xml
# RUN mvn -B clean package -Pnative  --no-transfer-progress
# RUN mvn package -Pnative -f pom.xml
RUN mvn --no-transfer-progress clean package -DskipTests

# The deployment Image
# FROM docker.io/oraclelinux:9-slim
FROM bellsoft/liberica-openjdk-alpine:17.0.6

EXPOSE 8081
EXPOSE 9904

#Configurar Zona Horaria
RUN apk upgrade --no-cache
RUN apk add --no-cache tzdata
ENV TZ="America/Guayaquil"

# Copy the native executable into the containers
WORKDIR /app
COPY --from=builder /build/target/config-1.0.0.jar .

#Download dependencias for monitoring
RUN wget -O /app/jmx_prometheus_javaagent-0.20.0.jar https://repo1.maven.org/maven2/io/prometheus/jmx/jmx_prometheus_javaagent/0.20.0/jmx_prometheus_javaagent-0.20.0.jar
COPY jmx_exporter_config.yaml /app/jmx_exporter_config.yaml

ENTRYPOINT [ "java", "-XX:MaxRAMPercentage=75.0", "-javaagent:/app/jmx_prometheus_javaagent-0.20.0.jar=9904:/app/jmx_exporter_config.yaml", "-jar", "/app/config-1.0.0.jar" ]

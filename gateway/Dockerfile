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
ARG MAVEN_DOWNLOAD_URL=https://dlcdn.apache.org/maven/maven-3/3.9.4/binaries/apache-maven-3.9.4-bin.tar.gz
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

COPY rc-settings.xml settings.xml
RUN sed -i "s|GIT_TOKEN|$PACKAGE_TOKEN |g" settings.xml

ARG POM_NAME=pom-dev.xml
ENV POM_NAME ${POM_NAME}

# Define argumentos para controlar el comportamiento y parametrizar la copia
ARG DOCKER_HUB=false
ARG ARTIFACT_VERSION=0.0.1-SNAPSHOT
# Lista de artefactos a copiar (separados por espacios)
ARG ARTIFACTS="share-dev"

RUN if [ "$DOCKER_HUB" = "false" ]; then \
      echo "Entorno local detectado, copiando artefactos locales..."; \
      for artifact in $ARTIFACTS; do \
          target_dir="$MAVEN_CONFIG/repository/com/kynsoft/finamer/$artifact/$ARTIFACT_VERSION"; \
          echo "Procesando artefacto: $artifact"; \
          mkdir -p "$target_dir" && \
          if [ -f libs/${artifact}-${ARTIFACT_VERSION}.jar ]; then \
              cp -v libs/${artifact}-${ARTIFACT_VERSION}.jar "$target_dir/"; \
          else \
              echo "❌ ERROR: No se encontró libs/${artifact}-${ARTIFACT_VERSION}.jar" && exit 1; \
          fi && \
          if [ -f libs/${artifact}-${ARTIFACT_VERSION}.pom ]; then \
              cp -v libs/${artifact}-${ARTIFACT_VERSION}.pom "$target_dir/"; \
          else \
              echo "⚠️  Advertencia: No se encontró libs/${artifact}-${ARTIFACT_VERSION}.pom"; \
          fi; \
      done; \
    fi

# Build
RUN mvn dependency:go-offline -f ${POM_NAME} -s settings.xml
# RUN mvn -B clean package -Pnative  --no-transfer-progress
# RUN mvn package -Pnative -f pom.xml
RUN mvn --no-transfer-progress clean package -DskipTests -f ${POM_NAME}

# The deployment Image
# FROM docker.io/oraclelinux:9-slim
FROM bellsoft/liberica-openjdk-alpine:17.0.6

EXPOSE 8080
EXPOSE 9904

#Configurar Zona Horaria
RUN apk upgrade --no-cache
RUN apk add --no-cache tzdata
ENV TZ="America/Guayaquil"

# Copy the native executable into the containers
WORKDIR /app
COPY --from=builder /build/target/gateway-1.0.0.jar .

#Download dependencias for monitoring
RUN wget -O /app/jmx_prometheus_javaagent-0.20.0.jar https://repo1.maven.org/maven2/io/prometheus/jmx/jmx_prometheus_javaagent/0.20.0/jmx_prometheus_javaagent-0.20.0.jar
COPY jmx_exporter_config.yaml /app/jmx_exporter_config.yaml

ENTRYPOINT [ "java", "-javaagent:/app/jmx_prometheus_javaagent-0.20.0.jar=9904:/app/jmx_exporter_config.yaml", "-jar", "/app/gateway-1.0.0.jar" ]

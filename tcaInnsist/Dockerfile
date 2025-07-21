FROM ghcr.io/graalvm/native-image:ol8-java17-22 AS builder

# Install tar and gzip to extract the Maven binaries
RUN microdnf update \
    && microdnf install --nodocs \
    tar \
    gzip \
    && microdnf clean all \
    && rm -rf /var/cache/yum

# Install Maven
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

# Setup environment vairables for maven resposiory
ARG PACKAGE_TOKEN
ENV PACKAGE_TOKEN ${PACKAGE_TOKEN}

COPY rc-settings.xml settings.xml
RUN sed -i "s|GIT_TOKEN|$PACKAGE_TOKEN |g" settings.xml

ARG ENVIRONMENT=dev
ENV ENVIRONMENT ${ENVIRONMENT}

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
RUN mvn dependency:go-offline -f pom.xml -s settings.xml -Denv=$ENVIRONMENT
RUN mvn clean package -DskipTests -f pom.xml -Denv=$ENVIRONMENT

# The deployment Image
# FROM docker.io/oraclelinux:9-slim
FROM bellsoft/liberica-openjdk-alpine:17.0.6

EXPOSE 9909
EXPOSE 9904

#Configurar Zona Horaria
RUN apk upgrade --no-cache
RUN apk add --no-cache tzdata busybox-extras traceroute
ENV TZ="America/Santo_Domingo"

# Copy the native executable into the containers
WORKDIR /app
COPY --from=builder /build/target/tcaInnsist-1.0.0.jar .

#Download dependencias for monitoring
RUN wget -O /app/jmx_prometheus_javaagent-0.20.0.jar https://repo1.maven.org/maven2/io/prometheus/jmx/jmx_prometheus_javaagent/0.20.0/jmx_prometheus_javaagent-0.20.0.jar
COPY jmx_exporter_config.yaml /app/jmx_exporter_config.yaml

ENTRYPOINT [ "java", "-XX:MaxRAMPercentage=75.0", "-javaagent:/app/jmx_prometheus_javaagent-0.20.0.jar=9904:/app/jmx_exporter_config.yaml", "-jar", "/app/tcaInnsist-1.0.0.jar" ]

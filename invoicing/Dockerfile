# Stage 1: Build the application
FROM ghcr.io/graalvm/native-image:ol8-java17-22 AS builder

# Install tar and gzip to extract the Maven binaries
RUN microdnf update \
    && microdnf install --nodocs tar gzip \
    && microdnf clean all \
    && rm -rf /var/cache/yum

# Install Maven
ARG USER_HOME_DIR="/root"
ARG SHA=deaa39e16b2cf20f8cd7d232a1306344f04020e1f0fb28d35492606f647a60fe729cc40d3cba33e093a17aed41bd161fe1240556d0f1b80e773abd408686217e
ARG MAVEN_DOWNLOAD_URL=https://dlcdn.apache.org/maven/maven-3/3.9.4/binaries/apache-maven-3.9.4-bin.tar.gz

RUN mkdir -p /usr/share/maven /usr/share/maven/ref \
    && curl -fsSL -o /tmp/apache-maven.tar.gz ${MAVEN_DOWNLOAD_URL} \
    && echo "${SHA}  /tmp/apache-maven.tar.gz" | sha512sum -c - \
    && tar -xzf /tmp/apache-maven.tar.gz -C /usr/share/maven --strip-components=1 \
    && rm -f /tmp/apache-maven.tar.gz \
    && ln -s /usr/share/maven/bin/mvn /usr/bin/mvn

ENV MAVEN_HOME=/usr/share/maven
ENV MAVEN_CONFIG="$USER_HOME_DIR/.m2"

# Set the working directory to /build
WORKDIR /build

# Copy the source code into the image for building
COPY . /build

# Setup environment variables for Maven repository
ARG PACKAGE_TOKEN
ENV PACKAGE_TOKEN=${PACKAGE_TOKEN}

COPY rc-settings.xml settings.xml
RUN sed -i "s|GIT_TOKEN|$PACKAGE_TOKEN|g" settings.xml

ARG POM_NAME
ENV POM_NAME=${POM_NAME}

# Build the project and download dependencies
RUN mvn dependency:go-offline -f ${POM_NAME} -s settings.xml
RUN mvn --no-transfer-progress clean package -DskipTests -f ${POM_NAME}

# Stage 2: Create the deployment image
FROM bellsoft/liberica-openjdk-alpine:17.0.6

EXPOSE 9909

# Configure Time Zone
RUN apk upgrade --no-cache
RUN apk add --no-cache tzdata
ENV TZ="America/Guayaquil"

# Copy the built jar file into the container
WORKDIR /app
COPY --from=builder /build/target/invoicing-1.0.0.jar .

# Ensure JAXB implementation is available at runtime
ENV JAVA_OPTS="-Djavax.xml.bind.JAXBContextFactory=org.glassfish.jaxb.runtime.v2.ContextFactory"

# Set the ENTRYPOINT to run the application with the correct classpath
ENTRYPOINT ["java", "-Djavax.xml.bind.JAXBContextFactory=org.glassfish.jaxb.runtime.v2.ContextFactory", "-jar", "/app/invoicing-1.0.0.jar"]
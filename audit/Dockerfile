# Etapa de construcción: utiliza Maven para compilar la aplicación
FROM maven:3.9.4-eclipse-temurin-17 AS builder

# Establece el directorio de trabajo
WORKDIR /build

# Copia el archivo de configuración de Maven
COPY rc-settings.xml settings.xml

# Configura variables de entorno para Maven y token de paquete
ARG PACKAGE_TOKEN
ENV PACKAGE_TOKEN=${PACKAGE_TOKEN}

# Reemplaza el token de configuración en settings.xml
RUN sed -i "s|GIT_TOKEN|$PACKAGE_TOKEN|g" settings.xml

RUN wget -O jmx_prometheus_javaagent.jar https://repo1.maven.org/maven2/io/prometheus/jmx/jmx_prometheus_javaagent/0.20.0/jmx_prometheus_javaagent-0.20.0.jar

# Copia los archivos de proyecto
COPY . /build

# Argumento para establecer el profile segun el ambiente
ARG ENVIRONMENT=dev
ENV ENVIRONMENT ${ENVIRONMENT}

# Copia las dependencias offline y el paquete
RUN mvn dependency:go-offline -f pom.xml -s settings.xml -Denv=$ENVIRONMENT
RUN mvn clean package -DskipTests -f pom.xml -Denv=$ENVIRONMENT

# Etapa final: imagen ligera de distroless
FROM gcr.io/distroless/java17-debian11

# Establece la zona horaria
ENV TZ="America/Guayaquil"

# Copia el JAR generado desde la etapa de construcción
COPY --from=builder /build/target/audit-1.0.0.jar /app/audit-1.0.0.jar

#Download dependencias for monitoring
COPY --from=builder /build/jmx_prometheus_javaagent.jar /app/jmx_prometheus_javaagent-0.20.0.jar
COPY jmx_exporter_config.yaml /app/jmx_exporter_config.yaml

# Expone el puerto que utiliza la aplicación
EXPOSE 9909
EXPOSE 9904

# Comando de inicio de la aplicación
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75.0", "-javaagent:/app/jmx_prometheus_javaagent-0.20.0.jar=9904:/app/jmx_exporter_config.yaml", "-jar", "/app/audit-1.0.0.jar"]

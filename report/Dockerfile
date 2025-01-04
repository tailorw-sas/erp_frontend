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

# Copia los archivos de proyecto
COPY . /build

# Argumento para obtener el nombre del POM de forma dinámica
ARG POM_NAME
ENV POM_NAME=${POM_NAME}

# Compila las dependencias offline y el paquete
RUN mvn dependency:go-offline -f ${POM_NAME} -s settings.xml
RUN mvn clean package -DskipTests -f ${POM_NAME}

# Etapa final: imagen ligera de distroless
FROM gcr.io/distroless/java17-debian11

# Establece la zona horaria
ENV TZ="America/Guayaquil"

# Copia el JAR generado desde la etapa de construcción
COPY --from=builder /build/target/report-1.0.0.jar /app/report-1.0.0.jar

# Expone el puerto que utiliza la aplicación
EXPOSE 9909

# Variables de entorno para depuración y monitoreo
ENV JAVA_TOOL_OPTIONS="-Xmx1024m -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/app/ -Dlogging.level.org.springframework=DEBUG"

# Comando de inicio de la aplicación con más memoria y recolección de basura mejorada
ENTRYPOINT ["java", "-Xmx1024m", "-XX:+UseG1GC", "-jar", "/app/report-1.0.0.jar"]
# Etapa de construcción
FROM maven:3.9.4-eclipse-temurin-17 AS builder

# Establece el directorio de trabajo
WORKDIR /build

# Copia los archivos de proyecto
COPY . /build

# Compila las dependencias offline y el paquete
RUN mvn dependency:go-offline -f ${POM_NAME} -s settings.xml
RUN mvn clean package -DskipTests -f ${POM_NAME}

# Etapa final: imagen ligera de distroless
FROM gcr.io/distroless/java17-debian11

# Establece la zona horaria
ENV TZ="America/Guayaquil"

# Copia el JAR generado desde la etapa de construcción
COPY --from=builder /build/target/invoicing-1.0.0.jar /app/invoicing-1.0.0.jar

# Copia el archivo Excel al contenedor
COPY src/main/resources/EstadoCuenta.xlsx /app/resources/EstadoCuenta.xlsx

# Expone el puerto que utiliza la aplicación
EXPOSE 9909

# Comando de inicio de la aplicación
ENTRYPOINT ["java", "-jar", "/app/invoicing-1.0.0.jar"]
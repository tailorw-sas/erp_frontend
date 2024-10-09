package com.kynsoft.finamer.payment.application.query.manageInvoice.sendAccountStatement;

import org.apache.commons.io.IOUtils;
import org.springframework.core.io.ClassPathResource;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;

public class FileService {

    public String convertExcelToBase64() throws IOException {
        InputStream inputStream = null;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        try {
            // Intentar cargar el archivo desde el classpath
            ClassPathResource classPathResource = new ClassPathResource("EstadoCuenta.xlsx");
            inputStream = classPathResource.getInputStream();
            System.out.println("Archivo cargado desde el classpath.");
        } catch (IOException e) {
            // Si no se encuentra en el classpath, cargar desde el sistema de archivos (Docker)
            System.out.println("Archivo no encontrado en el classpath, intentando cargar desde el sistema de archivos.");
            inputStream = new FileInputStream("/app/resources/EstadoCuenta.xlsx");
        }

        // Convertir el InputStream a un array de bytes
        IOUtils.copy(inputStream, byteArrayOutputStream);
        byte[] fileBytes = byteArrayOutputStream.toByteArray();

        // Convertir el array de bytes a Base64
        return Base64.getEncoder().encodeToString(fileBytes);
    }
}
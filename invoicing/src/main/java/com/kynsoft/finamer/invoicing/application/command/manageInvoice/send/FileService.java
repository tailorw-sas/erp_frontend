package com.kynsoft.finamer.invoicing.application.command.manageInvoice.send;

import org.springframework.core.io.ClassPathResource;
import org.apache.commons.io.IOUtils;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;

public class FileService {

    public String convertExcelToBase64() throws IOException {
        // Cargar el archivo desde el classpath (resources)
        ClassPathResource classPathResource = new ClassPathResource("EstadoCuenta.xlsx");

        // Leer el contenido del archivo en un InputStream
        try (InputStream inputStream = classPathResource.getInputStream()) {
            // Convertir el InputStream a un array de bytes
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            IOUtils.copy(inputStream, byteArrayOutputStream);
            byte[] fileBytes = byteArrayOutputStream.toByteArray();

            // Convertir el array de bytes a Base64
            return Base64.getEncoder().encodeToString(fileBytes);
        }
    }
}
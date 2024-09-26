package com.kynsof.share.core.application.ftp;

import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.entity.mime.MultipartEntityBuilder;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.ContentType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Service
public class FtpService {
    @Value("${ftp.api.url:http://localhost:8097/api/ftp}")
    private String ftpApiUrl;

    public String sendFile(InputStream inputStream, String fileName, String server, String user, String password, int port) {
        String uploadUrl = ftpApiUrl + "/upload"; // Asegúrate de que la URL sea correcta

        try (CloseableHttpClient client = HttpClients.createDefault()) {
            // Crear la solicitud POST con multipart/form-data
            HttpPost post = new HttpPost(uploadUrl);

            // Construir la entidad multipart
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.addBinaryBody("file", inputStream, ContentType.DEFAULT_BINARY, fileName);
            builder.addTextBody("server", server);
            builder.addTextBody("user", user);
            builder.addTextBody("password", password);
            builder.addTextBody("port", String.valueOf(port)); // Convertir el puerto a String

            // Asignar la entidad a la solicitud
            post.setEntity(builder.build());

            // Enviar la solicitud y manejar la respuesta
            try (CloseableHttpResponse response = client.execute(post)) {
                int statusCode = response.getCode();
                String responseBody = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);

                // Evaluar la respuesta del servidor
                if (statusCode == 200) {
                    return "Archivo subido exitosamente: " + responseBody;
                } else {
                    return "Error al subir el archivo: " + statusCode + " - " + responseBody;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            return "Excepción al subir el archivo: " + e.getMessage();
        }
    }
}
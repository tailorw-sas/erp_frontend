package com.kynsof.share.core.application.ftp;

import com.kynsof.share.core.domain.service.IFtpService;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.entity.mime.MultipartEntityBuilder;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Service
public class FtpService implements IFtpService {

    @Value("${ftp.api.url:http://localhost:8097/api/ftp}")
    private String ftpApiUrl;

    public void sendFile(InputStream inputStream, String fileName, String server, String user, String password, int port) {
        String uploadUrl = ftpApiUrl + "/upload";

        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost post = createHttpPost(uploadUrl, inputStream, fileName, server, user, password, port);

            // Enviar la solicitud y manejar la respuesta
            try (CloseableHttpResponse response = client.execute(post)) {
                handleResponse(response);
            }

        } catch (Exception e) {
            handleError(e);
        }
    }

    private HttpPost createHttpPost(String url, InputStream inputStream, String fileName, String server, String user,
                                    String password, int port) {
        // Configurar la solicitud POST con los datos necesarios
        HttpPost post = new HttpPost(url);
        MultipartEntityBuilder builder = MultipartEntityBuilder.create()
                .addBinaryBody("file", inputStream, ContentType.DEFAULT_BINARY, fileName)
                .addTextBody("server", server)
                .addTextBody("user", user)
                .addTextBody("password", password)
                .addTextBody("port", String.valueOf(port)); // Convertir el puerto a String

        post.setEntity(builder.build());
        return post;
    }

    private void handleResponse(CloseableHttpResponse response) throws Exception {
        int statusCode = response.getCode();
        String responseBody = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);

        // Evaluar la respuesta del servidor
        if (statusCode == 200) {
            System.out.println("Archivo subido exitosamente: " + responseBody);
        } else {
            System.err.println("Error al subir el archivo: " + statusCode + " - " + responseBody);
            throw new RuntimeException("Error al subir el archivo: " + statusCode + " - " + responseBody);
        }
    }

    private void handleError(Exception e) {
        System.err.println("Excepción al subir el archivo: " + e.getMessage());
        e.printStackTrace();
        // Puedes lanzar una RuntimeException si deseas propagar el error
        throw new RuntimeException("Excepción al subir el archivo: " + e.getMessage(), e);
    }
}
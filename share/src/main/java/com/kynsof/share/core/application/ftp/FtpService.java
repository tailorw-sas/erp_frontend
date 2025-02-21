package com.kynsof.share.core.application.ftp;

import com.kynsof.share.core.domain.service.IFtpService;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.entity.mime.MultipartEntityBuilder;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.io.HttpClientResponseHandler;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Service
public class FtpService implements IFtpService {

    private static final Logger log = LoggerFactory.getLogger(FtpService.class);

    @Value("${ftp.api.url:http://localhost:8097/api/ftp}")
    private String ftpApiUrl;

    public void sendFile(InputStream inputStream, String fileName, String server, String user, String password, int port, String path) {
        String uploadUrl = ftpApiUrl + "/upload";

        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost post = createHttpPost(uploadUrl, inputStream, fileName, server, user, password, port, path);

            // Usar HttpClientResponseHandler para manejar la respuesta de manera segura
            HttpClientResponseHandler<String> responseHandler = response -> {
                int statusCode = response.getCode();
                String responseBody = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);

                if (statusCode == 200) {
                    log.info("✅ Archivo subido exitosamente: {}", responseBody);
                    return responseBody;
                } else {
                    log.error("❌ Error al subir el archivo: {} - {}", statusCode, responseBody);
                    throw new RuntimeException("Error al subir el archivo: " + statusCode + " - " + responseBody);
                }
            };

            // Ejecutar la solicitud y manejar la respuesta con el handler
            client.execute(post, responseHandler);

        } catch (Exception e) {
            handleError(e);
        }
    }

    private HttpPost createHttpPost(String url, InputStream inputStream, String fileName, String server, String user,
                                    String password, int port, String path) {
        HttpPost post = new HttpPost(url);
        MultipartEntityBuilder builder = MultipartEntityBuilder.create()
                .addBinaryBody("file", inputStream, ContentType.DEFAULT_BINARY, fileName)
                .addTextBody("server", server)
                .addTextBody("user", user)
                .addTextBody("password", password)
                .addTextBody("path", path)
                .addTextBody("port", String.valueOf(port));

        post.setEntity(builder.build());
        return post;
    }

    private void handleError(Exception e) {
        log.error("❌ Excepción al subir el archivo: {}", e.getMessage(), e);
        throw new RuntimeException("Excepción al subir el archivo: " + e.getMessage(), e);
    }
}
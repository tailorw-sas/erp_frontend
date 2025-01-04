package com.kynsof.share.core.application.mailjet;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
@Configuration
public class MailService {

    @Value("${mail.api.url:http://localhost:8097/api/mail/send}")
    private String mailApiUrl;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public MailService() {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
    }

    public String sendMail(SendMailJetEMailRequest emailRequest) {
        try {
            // Convertir la solicitud a JSON
            String requestBody = objectMapper.writeValueAsString(emailRequest);

            // Configurar la solicitud HTTP
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(mailApiUrl))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            // Enviar la solicitud y procesar la respuesta
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            // Manejo de la respuesta
            if (response.statusCode() == 200) {
                return "Correo enviado exitosamente: " + response.body();
            } else {
                return "Error al enviar el correo: " + response.statusCode() + " - " + response.body();
            }

        } catch (Exception e) {
            e.printStackTrace();
            return "Excepción al enviar el correo: " + e.getMessage();
        }
    }
}

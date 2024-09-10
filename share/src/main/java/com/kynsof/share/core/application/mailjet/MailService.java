package com.kynsof.share.core.application.mailjet;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
public class MailService {

    private static final String MAIL_API_URL = "http://localhost:8080/api/mail/send"; // Reemplaza con la URL de tu API
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
                    .uri(new URI(MAIL_API_URL))
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

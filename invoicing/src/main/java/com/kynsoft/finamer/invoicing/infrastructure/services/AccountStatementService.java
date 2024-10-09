package com.kynsoft.finamer.invoicing.infrastructure.services;

import com.kynsoft.finamer.invoicing.domain.dto.SendAccountStatementRequest;
import com.kynsoft.finamer.invoicing.domain.dto.SendAccountStatementResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class AccountStatementService {

    private final RestTemplate restTemplate;

    @Value("${account.statement.service.url:http://localhost:9909}")
    private String serviceUrl;

    public AccountStatementService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public SendAccountStatementResponse sendAccountStatement(SendAccountStatementRequest request) {
        String url = serviceUrl + "/api/payment/manage-invoice/send-account-statement";  // Usar la URL configurada

        // Crear cabeceras para la solicitud
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Crear la entidad de la solicitud con el cuerpo (request) y las cabeceras
        HttpEntity<SendAccountStatementRequest> entity = new HttpEntity<>(request, headers);

        // Enviar la solicitud POST al endpoint del controlador
        ResponseEntity<SendAccountStatementResponse> response = restTemplate.exchange(
                url, HttpMethod.POST, entity, SendAccountStatementResponse.class);

        return response.getBody();
    }
}
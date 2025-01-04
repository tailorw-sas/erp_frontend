package com.kynsof.share.core.application.invoice;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.request.SearchRequest;
import com.kynsof.share.core.domain.request.SortTypeEnum;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

@Service
@Configuration
public class InvoicehttpService {

    //@Value("${mail.api.url:http://localhost:9199/api/manage-invoice/search}")
    @Value("${mail.api.url:http://localhost:9199/api/manage-invoice/a9eb9f2e-9d91-41e7-86e1-b2f94ad44261}")
    private String mailApiUrl;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public InvoicehttpService() {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
    }

    public String sendMail() {
        try {
            // Convertir la solicitud a JSON
            SearchRequest requestBody = new SearchRequest();
            List<FilterCriteria> filter = new ArrayList<>();
            String query = "";
            Integer pageSize = 20;
            Integer page = 0;
            String sortBy = "";
            SortTypeEnum sortType = SortTypeEnum.ASC;

            requestBody.setFilter(filter);
            requestBody.setQuery(query);
            requestBody.setPageSize(pageSize);
            requestBody.setPage(page);
            requestBody.setSortBy(sortBy);
            requestBody.setSortType(sortType);

            String requestSearchBody = objectMapper.writeValueAsString(requestBody);

            // Configurar la solicitud HTTP
//            HttpRequest request = HttpRequest.newBuilder()
//                    .uri(new URI(mailApiUrl))
//                    .header("Content-Type", "application/json")
//                    .POST(HttpRequest.BodyPublishers.ofString(requestSearchBody))
//                    .build();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(mailApiUrl))
                    .header("Content-Type", "application/json")
                    .GET()
                    .build();

            // Enviar la solicitud y procesar la respuesta
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            JsonNode rootNode = objectMapper.readTree(response.body());

            // Manejo de la respuesta
            if (response.statusCode() == 200) {
                System.err.println(rootNode.toString());
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

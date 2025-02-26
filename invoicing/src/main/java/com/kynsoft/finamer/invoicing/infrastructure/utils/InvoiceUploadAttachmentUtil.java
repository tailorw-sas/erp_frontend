package com.kynsoft.finamer.invoicing.infrastructure.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kynsof.share.core.domain.response.ApiResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.LinkedHashMap;

@Component
public class InvoiceUploadAttachmentUtil {
    @Value("${file.service.url}")
    private String UPLOAD_FILE_URL;
    private final RestTemplate restTemplate;

    private ObjectMapper objectMapper;

    public InvoiceUploadAttachmentUtil(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        this.objectMapper = new ObjectMapper();
    }

    public LinkedHashMap<String, String> uploadAttachmentContent(String fileName, byte[] fileContent) throws JsonProcessingException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        // Crear las cabeceras para el archivo
        HttpHeaders fileHeaders = new HttpHeaders();
        fileHeaders.setContentType(MediaType.APPLICATION_PDF); // Asegurar que se sube como PDF
        fileHeaders.setContentDisposition(ContentDisposition.builder("form-data")
                .name("file")
                .filename(fileName)
                .build());

        // Crear la parte del archivo con los encabezados correctos
        HttpEntity<byte[]> fileEntity = new HttpEntity<>(fileContent, fileHeaders);

        // Construir el cuerpo de la solicitud multipart
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", fileEntity);

        // Construir la solicitud HTTP final
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        // Enviar la solicitud al servidor (MinIO o servicio S3)
        System.out.println(UPLOAD_FILE_URL);
        System.out.println("Headers: " + requestEntity.getHeaders());
        System.out.println("Body: " + requestEntity.getBody());

        ResponseEntity<String> response = restTemplate.postForEntity(UPLOAD_FILE_URL, requestEntity, String.class);

        // Validar si la respuesta es exitosa
        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Error subiendo el archivo: " + response.getStatusCode());
        }

        // Procesar la respuesta JSON
        ApiResponse apiResponse = objectMapper.readValue(response.getBody(), ApiResponse.class);
        return (LinkedHashMap<String, String>) apiResponse.getData();
    }
}

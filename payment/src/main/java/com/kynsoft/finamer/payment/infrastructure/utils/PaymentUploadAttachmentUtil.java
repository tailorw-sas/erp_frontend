package com.kynsoft.finamer.payment.infrastructure.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kynsof.share.core.domain.response.ApiResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.LinkedHashMap;
import java.util.Optional;

@Component
public class PaymentUploadAttachmentUtil {
    @Value("${payment.file.service.url}")
    private String UPLOAD_FILE_URL;
    private final RestTemplate restTemplate;

    private ObjectMapper objectMapper;

    public PaymentUploadAttachmentUtil(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        this.objectMapper = new ObjectMapper();
    }

    public LinkedHashMap<String,String> uploadAttachmentContent(String fileName,byte[] fileContent) throws JsonProcessingException {
        ResponseEntity<String> response = restTemplate.postForEntity(UPLOAD_FILE_URL, this.createBody(fileName,fileContent), String.class);
        ApiResponse apiResponse = objectMapper.readValue(response.getBody(), ApiResponse.class);
        LinkedHashMap<String,String> saveFileS3Message = (LinkedHashMap) apiResponse.getData();
        return saveFileS3Message;
    }
    
    private HttpEntity<MultiValueMap<String, Object>> createBody(String fileName, byte[] fileContent) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MediaType mediaType = resolveMediaType(fileName);

        HttpHeaders fileHeaders = new HttpHeaders();
        fileHeaders.setContentType(mediaType);
        fileHeaders.setContentDisposition(
                ContentDisposition.builder("form-data")
                        .name("file")
                        .filename(fileName)
                        .build()
        );

        HttpEntity<byte[]> fileEntity = new HttpEntity<>(fileContent, fileHeaders);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", fileEntity);

        return new HttpEntity<>(body, headers);
    }

    private MediaType resolveMediaType(String fileName) {
        String extension = Optional.ofNullable(fileName)
                .filter(f -> f.contains("."))
                .map(f -> f.substring(fileName.lastIndexOf('.') + 1))
                .map(String::toLowerCase)
                .orElse("");

        return switch (extension) {
            case "pdf" -> MediaType.APPLICATION_PDF;
            case "txt" -> MediaType.TEXT_PLAIN;
            case "doc", "docx" -> MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
            case "xls", "xlsx" -> MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            default -> MediaType.APPLICATION_OCTET_STREAM;
        };
    }

}

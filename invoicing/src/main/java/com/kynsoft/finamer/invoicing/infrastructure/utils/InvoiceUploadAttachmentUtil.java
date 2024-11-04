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

    public LinkedHashMap<String,String> uploadAttachmentContent(String fileName,byte[] fileContent) throws JsonProcessingException {
        ResponseEntity<String> response = restTemplate.postForEntity(UPLOAD_FILE_URL, this.createBody(fileName,fileContent), String.class);
        ApiResponse apiResponse = objectMapper.readValue(response.getBody(), ApiResponse.class);
        LinkedHashMap<String,String> saveFileS3Message = (LinkedHashMap) apiResponse.getData();
        return saveFileS3Message;
    }

    private HttpEntity<MultiValueMap<String, Object>> createBody(String fileName,byte[] fileContent){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        MultiValueMap<String, String> contentDispositionMap = new LinkedMultiValueMap<>();
        ContentDisposition contentDisposition = ContentDisposition
                .builder("form-data")
                .name("file")
                .filename(fileName)
                .build();
        contentDispositionMap.add(HttpHeaders.CONTENT_DISPOSITION, contentDisposition.toString());
        HttpEntity<byte[]> fileEntity = new HttpEntity<>(fileContent, contentDispositionMap);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

        body.add("file", fileEntity);

        return  new HttpEntity<>(body, headers);
    }
}

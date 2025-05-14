package com.kynsoft.finamer.payment.infrastructure.services.http;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kynsof.share.core.domain.exception.BusinessNotFoundException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.exception.GlobalBusinessException;
import com.kynsof.share.core.domain.http.entity.income.CreateAntiToIncomeRequest;
import com.kynsof.share.core.domain.http.entity.income.CreateIncomeFromPaymentMessage;
import com.kynsof.share.core.domain.response.ErrorField;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.logging.Level;
import java.util.logging.Logger;

@Slf4j
@Service
public class CreateIncomeHttpService {

    private final RestTemplate restTemplate;

//    @Value("${booking.invoice.service:http://localhost:9199}")
    @Value("${booking.invoice.service:http://invoicing.finamer.svc.cluster.local:9909}")
    private String serviceUrl;

    public CreateIncomeHttpService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public CreateIncomeFromPaymentMessage sendCreateIncomeRequest(CreateAntiToIncomeRequest request) {
        String url = serviceUrl + "/api/income/anti-to-income";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<CreateAntiToIncomeRequest> entity = new HttpEntity<>(request, headers);

        try {
            log.info("Sending request to URL: {}", url);
            log.info("Request Payload as JSON: {}", new ObjectMapper().writeValueAsString(request));

            // Realizar la solicitud POST
            ResponseEntity<CreateIncomeFromPaymentMessage> response = restTemplate.postForEntity(url, entity, CreateIncomeFromPaymentMessage.class);

            if (!HttpStatus.OK.equals(response.getStatusCode())) {
                log.error("Failed to create income. Status: {}, Response: {}", response.getStatusCode(), response);
                throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.INCOME_CREATE_PROCESS_FAILED, new ErrorField("id", DomainErrorMessage.INCOME_CREATE_PROCESS_FAILED.getReasonPhrase())));
            }

            return response.getBody();

        } catch (RestClientException e) {
            log.error("Exception occurred while creating income: {}", e.getMessage(), e);
            throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.INCOME_CREATE_PROCESS_FAILED, new ErrorField("id", DomainErrorMessage.INCOME_CREATE_PROCESS_FAILED.getReasonPhrase())));
        } catch (JsonProcessingException e) {
            log.error("Exception occurred while creating income: {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }
}

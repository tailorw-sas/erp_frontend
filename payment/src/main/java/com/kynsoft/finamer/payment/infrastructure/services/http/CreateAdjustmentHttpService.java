package com.kynsoft.finamer.payment.infrastructure.services.http;

import com.kynsof.share.core.domain.exception.BusinessNotFoundException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.exception.GlobalBusinessException;
import com.kynsof.share.core.domain.http.entity.income.adjustment.CreateAntiToIncomeAdjustmentRequest;
import com.kynsof.share.core.domain.response.ErrorField;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Service
public class CreateAdjustmentHttpService {

    private final RestTemplate restTemplate;

//    @Value("${booking.invoice.service:http://localhost:9199}")
    @Value("${booking.invoice.service:http://invoicing.finamer.svc.cluster.local:9909}")
    private String serviceUrl;

    public CreateAdjustmentHttpService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String sendCreateIncomeRequest(CreateAntiToIncomeAdjustmentRequest request) {
        try {
            String url = serviceUrl + "/api/income-adjustment";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<CreateAntiToIncomeAdjustmentRequest> entity = new HttpEntity<>(request, headers);

            // Realizar la solicitud POST
            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);

            if (!HttpStatus.OK.equals(response.getStatusCode())) {
                throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.INCOME_CREATE_PROCESS_FAILED, new ErrorField("id", DomainErrorMessage.INCOME_CREATE_PROCESS_FAILED.getReasonPhrase())));
            }
            return response.getBody();
        } catch (RestClientException e) {
            throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.INCOME_CREATE_PROCESS_FAILED, new ErrorField("id", DomainErrorMessage.INCOME_CREATE_PROCESS_FAILED.getReasonPhrase())));
        }
    }
}

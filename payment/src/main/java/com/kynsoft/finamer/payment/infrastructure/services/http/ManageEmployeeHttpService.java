package com.kynsoft.finamer.payment.infrastructure.services.http;

import com.kynsof.share.core.domain.exception.BusinessNotFoundException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.exception.GlobalBusinessException;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsoft.finamer.payment.application.query.http.setting.manageEmployee.ManageEmployeeRequest;
import com.kynsoft.finamer.payment.application.query.http.setting.manageEmployee.ManageEmployeeResponse;
import com.kynsoft.finamer.payment.application.query.http.setting.paymenteTransactionType.ManagePaymentTransactionTypeRequest;
import com.kynsoft.finamer.payment.application.query.http.setting.paymenteTransactionType.ManagePaymentTransactionTypeResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Service
public class ManageEmployeeHttpService {

    private final RestTemplate restTemplate;

    @Value("${setting.service.url:http://localhost:9094}")
    private String serviceUrl;

    public ManageEmployeeHttpService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public ManageEmployeeResponse sendAccountStatement(ManageEmployeeRequest request) {
        try {
            String url = serviceUrl + "/api/manage-employee/http-replicate/" + request.getId();

            // Crear cabeceras para la solicitud
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // Crear la entidad de la solicitud con el cuerpo (request) y las cabeceras
            HttpEntity<ManageEmployeeRequest> entity = new HttpEntity<>(request, headers);

            // Enviar la solicitud POST al endpoint del controlador
            ResponseEntity<ManageEmployeeResponse> response = restTemplate.exchange(
                    url, HttpMethod.GET, entity, ManageEmployeeResponse.class);

            if (!HttpStatus.OK.equals(response.getStatusCode())) {
                throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.MANAGE_EMPLOYEE_NOT_FOUND, new ErrorField("id", DomainErrorMessage.MANAGE_EMPLOYEE_NOT_FOUND.getReasonPhrase())));
            }
            return response.getBody();
        } catch (RestClientException e) {
            throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.MANAGE_EMPLOYEE_NOT_FOUND, new ErrorField("id", DomainErrorMessage.MANAGE_EMPLOYEE_NOT_FOUND.getReasonPhrase())));
        }
    }
}

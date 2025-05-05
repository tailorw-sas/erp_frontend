package com.kynsoft.finamer.insis.infrastructure.services.http;

import com.kynsof.share.core.domain.exception.BusinessNotFoundException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.exception.GlobalBusinessException;
import com.kynsof.share.core.domain.kafka.entity.importInnsist.ImportInnsistKafka;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsoft.finamer.insis.infrastructure.model.http.importBooking.ImportInnsistRequest;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Service
public class ImportRoomRateHttpService {

    private final RestTemplate restTemplate;

    public ImportRoomRateHttpService(RestTemplate restTemplate){
        this.restTemplate = restTemplate;
    }

    public void sendRoomRatesToImport(ImportInnsistRequest request){
        try{
            String url = "http://192.168.100.62:9905" + "/api/import-innsist/import";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<ImportInnsistRequest> entity = new HttpEntity<>(request, headers);

            ResponseEntity<Void> response = restTemplate.exchange(url, HttpMethod.POST, entity, Void.class);

            if(!HttpStatus.OK.equals(response.getStatusCode())){
                throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.BOOKING_NOT_FOUND_, new ErrorField("id", DomainErrorMessage.BOOKING_NOT_FOUND_.getReasonPhrase())));
            }
        }catch (RestClientException e) {
            throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.BOOKING_NOT_FOUND_, new ErrorField("id", DomainErrorMessage.BOOKING_NOT_FOUND_.getReasonPhrase())));
        }
    }
}

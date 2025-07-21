package com.kynsoft.finamer.insis.infrastructure.services.http;

import com.kynsof.share.utils.DateConvert;
import com.kynsoft.finamer.insis.infrastructure.services.http.entities.request.SearchRateBetweenInvoiceDateRequest;
import com.kynsoft.finamer.insis.infrastructure.services.http.entities.request.SycnRateByInvoiceDateRequest;
import com.kynsoft.finamer.insis.infrastructure.services.http.entities.response.SearchRateBetweenInvoiceDateResponse;
import com.kynsoft.finamer.insis.infrastructure.services.http.entities.response.SyncRateByInvoiceDateMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.UUID;

@Service
public class SyncRoomRateHttpService {

    private final RestTemplate restTemplate;

    @Value("${tcainnsist.service.url:http://localhost:9910}")
    private String tcaServiceUrl;

    public SyncRoomRateHttpService(RestTemplate restTemplate){
        this.restTemplate = restTemplate;
    }

    public SyncRateByInvoiceDateMessage syncRoomRatesFromTca(UUID processId,
                                     String hotel,
                                     LocalDate invoiceDate){
        try{
            String url = tcaServiceUrl + "/api/room-rate/sync";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            SycnRateByInvoiceDateRequest request = new SycnRateByInvoiceDateRequest(
                    processId,
                    hotel,
                    DateConvert.convertLocalDateToString(invoiceDate, DateConvert.getIsoLocalDateFormatter())
            );

            HttpEntity<SycnRateByInvoiceDateRequest> requestHttpEntity = new HttpEntity<>(request, headers);

            ResponseEntity<SyncRateByInvoiceDateMessage> response = restTemplate.exchange(url,
                    HttpMethod.POST,
                    requestHttpEntity,
                    SyncRateByInvoiceDateMessage.class);
            if(response.getStatusCode().is2xxSuccessful() && response.getBody() != null){
                return response.getBody();
            } else {
                throw new RuntimeException("Error sincronizando room rates. HTTP status: " + response.getStatusCode());
            }

        }catch (Exception ex){
            throw new RuntimeException("Error al invocar servicio TCA", ex);
        }
    }

    public SearchRateBetweenInvoiceDateResponse syncRoomRatesFromTca(UUID processId,
                                                             String hotel,
                                                             LocalDate fromInvoiceDate,
                                                             LocalDate toInvoiceDate){
        try{
            String url = tcaServiceUrl + "/api/room-rate/search-between-invoice-date";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            SearchRateBetweenInvoiceDateRequest request = new SearchRateBetweenInvoiceDateRequest(
                    processId,
                    hotel,
                    DateConvert.convertLocalDateToString(fromInvoiceDate, DateConvert.getIsoLocalDateFormatter()),
                    DateConvert.convertLocalDateToString(toInvoiceDate, DateConvert.getIsoLocalDateFormatter())
            );

            HttpEntity<SearchRateBetweenInvoiceDateRequest> requestHttpEntity = new HttpEntity<>(request, headers);

            ResponseEntity<SearchRateBetweenInvoiceDateResponse> response = restTemplate.exchange(url,
                    HttpMethod.POST,
                    requestHttpEntity,
                    SearchRateBetweenInvoiceDateResponse.class);
            if(response.getStatusCode().is2xxSuccessful() && response.getBody() != null){
                return response.getBody();
            } else {
                throw new RuntimeException("Error sincronizando room rates. HTTP status: " + response.getStatusCode());
            }

        }catch (Exception ex){
            throw new RuntimeException("Error al invocar servicio TCA", ex);
        }
    }
}

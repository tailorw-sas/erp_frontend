package com.kynsof.share.core.application.payment.infrastructure.service;

import com.kynsof.share.core.application.payment.domain.placeToPlay.request.PaymentData;
import com.kynsof.share.core.application.payment.domain.placeToPlay.response.PaymentResponse;
import com.kynsof.share.core.application.payment.domain.placeToPlay.response.Transactions;
import com.kynsof.share.core.application.payment.domain.placeToPlay.response.TransactionsResponse;
import com.kynsof.share.core.application.payment.domain.placeToPlay.response.TransactionsState;
import com.kynsof.share.core.application.payment.domain.service.IPaymentServiceClient;
import com.kynsof.share.core.application.payment.infrastructure.service.config.PaymentServiceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;

@Service
class PaymentServiceClient implements IPaymentServiceClient {

    private static final Logger logger = LoggerFactory.getLogger(PaymentServiceClient.class);

    //  private final PlacetoPayFeignClient placetoPayFeignClient;


    private final RestTemplate restTemplate;
    private final PaymentServiceConfig paymentServiceConfig;


    public PaymentServiceClient(RestTemplate restTemplate, PaymentServiceConfig paymentServiceConfig) {
        this.restTemplate = restTemplate;
        this.paymentServiceConfig = paymentServiceConfig;
        // this.placetoPayFeignClient = placetoPayFeignClient;

    }

    @Override
    public boolean checkPlaceToPayPayment(String paymentCode) {
        try {
            return true;
//            String civilRegistrationId = this.civilRegistrations();
//            ResponseEntity<String> response = placetoPayFeignClient.checkPayment(civilRegistrationId, paymentCode);
//            JsonNode jsonResponse = jsonReader.readTree(response.getBody());
//            return Objects.equals("APPROVED", jsonResponse.path("currentStatus").asText());
        } catch (Exception ex) {
            logger.error("Error checking payment in PlacetoPay: ", ex);
            return false;
        }
    }

    @Override
    public TransactionsResponse getAllTransactionsClient(String idClient, Integer Page, Integer PageSize) {
        try {
//            String civilRegistrationId = this.civilRegistrations();
//            ResponseEntity<String> response = this.placetoPayFeignClient.getAllTransactionsClient(civilRegistrationId, idClient, Page, PageSize);
//            JsonNode jsonResponse = jsonReader.readTree(response.getBody());
//            ObjectMapper objectMapper = new ObjectMapper();
//            TransactionsResponse identity = objectMapper.readValue(jsonResponse.toString(), TransactionsResponse.class);
//            return identity;
            return null;
        } catch (Exception ex) {
            return null;
        }
    }

    @Override
    public Transactions checkPlaceToPayPaymentT(String paymentCode) {
        Transactions transactions = new Transactions();
        try {
//            String civilRegistrationId = this.civilRegistrations();
//            ResponseEntity<String> response = placetoPayFeignClient.checkPayment(civilRegistrationId, paymentCode);
//            JsonNode jsonResponse = jsonReader.readTree(response.getBody());
//
//            transactions.setCurrentStatus(jsonResponse.path("currentStatus").asText());
//            transactions.setCivilRegistrationId(jsonResponse.path("civilRegistrationId").asText());
//            transactions.setTransactionCode(jsonResponse.path("transactionCode").asText());
//            transactions.setReference(jsonResponse.path("reference").asText());
//            transactions.setRequestId(jsonResponse.path("requestId").asText());
//            transactions.setError(false);
            return transactions;
        } catch (Exception ex) {
            transactions.setMessage("Error, getting data for the paymentCode: " + paymentCode);
            transactions.setError(true);

            return transactions;
        }
    }


    @Override
    public String civilRegistrations() {
        try {
//            JsonNode jsonResponse = jsonReader.readTree(placetoPayFeignClient.civilRegistrations().getBody());
//            return jsonResponse.get(0).get("civilRegistrationId").asText();
            return null;
        } catch (Exception e) {
            return "798de007-ce14-4ea7-ba02-94877625ce6d";
        }
    }

    @Override
    public PaymentResponse paymentTransactions(PaymentData paymentData) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<PaymentData> request = new HttpEntity<>(paymentData, headers);
            String serviceUrl = "http://"+paymentServiceConfig.getPaymentServiceBaseUrl()+":"+paymentServiceConfig.getPaymentServicePort()+ "/placetopay/" +
                    paymentServiceConfig.getPaymentServiceClientId() + "/transactions";
            paymentData.setExpiration(getDateTimePlus15MinutesAsString());
                logger.error("URL-ERROR:" + serviceUrl);
            ResponseEntity<PaymentResponse> responseEntity = restTemplate.exchange(
                    serviceUrl,
                    HttpMethod.POST,
                    request,
                    PaymentResponse.class);

            logger.error("Response:" + responseEntity.getStatusCode());

            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                return responseEntity.getBody();
            } else {

                throw new RuntimeException("Respuesta no exitosa del servidor: " + responseEntity.getStatusCode());
            }
        } catch (Exception e) {
            throw new RuntimeException("Falló al ejecutar la consulta de pago.", e);
        }

    }

    @Override
    public TransactionsState getTransactionsState(Integer requestId) {
        try {
            String serviceUrl = String.format("%s/placetopay/%s/information/%s",
                    "http://"+paymentServiceConfig.getPaymentServiceBaseUrl()+":"+paymentServiceConfig.getPaymentServicePort(),
                    paymentServiceConfig.getPaymentServiceClientId(),
                    requestId);
            logger.error("URL-PAYMENT:" + serviceUrl);
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            HttpEntity<String> requestEntity = new HttpEntity<>("", headers);

            ResponseEntity<TransactionsState> response = restTemplate.exchange(
                    serviceUrl,
                    HttpMethod.POST,
                    requestEntity,
                    TransactionsState.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return response.getBody();
            } else {
                throw new RuntimeException("Respuesta no exitosa del servidor: " + response.getStatusCode());
            }

        } catch (Exception e) {
            throw new RuntimeException("Falló al obtener el estado de la transacción.", e);
        }
    }

    public String getDateTimePlus15MinutesAsString() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime newTime = now.plusMinutes(paymentServiceConfig.getPaymentExpirationMinutes());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return newTime.format(formatter);
    }
}

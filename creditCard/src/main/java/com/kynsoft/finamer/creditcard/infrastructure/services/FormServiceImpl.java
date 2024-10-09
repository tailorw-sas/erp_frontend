package com.kynsoft.finamer.creditcard.infrastructure.services;

import com.kynsoft.finamer.creditcard.application.query.objectResponse.CardNetSessionResponse;
import com.kynsoft.finamer.creditcard.application.query.objectResponse.ManageMerchantResponse;
import com.kynsoft.finamer.creditcard.domain.dto.PaymentRequestDto;
import com.kynsoft.finamer.creditcard.domain.services.IFormService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class FormServiceImpl implements IFormService {
    @Value("${redirect.private.key}")
    private String privateKey;

    @Override
    public ResponseEntity<String> redirectToBlueMerchant(ManageMerchantResponse response, PaymentRequestDto requestDto) {
        try {
            // Extraer los parámetros del objeto PaymentRequest
            //TODO: aquí la idea es que la info del merchant se tome de merchant, b2bparter y merchantConfig
            String merchantId = response.getMerchantConfigResponse().getMerchantNumber(); //Campo merchantNumber de Merchant Config
            String merchantName = response.getMerchantConfigResponse().getName(); //Campo name de Merchant Config
            String merchantType = response.getMerchantConfigResponse().getMerchantType(); //Campo merchantType de Merchant Config
            String currencyCode = "$"; //Valor $ por ahora
            String orderNumber = requestDto.getOrderNumber(); //Viene en el request
            String amount = requestDto.getAmount(); //Viene en el request
            String itbis = "000"; //Valor 000 por defecto
            String approvedUrl = response.getMerchantConfigResponse().getSuccessUrl(); //Campo successUrl de Merchant Config
            String declinedUrl = response.getMerchantConfigResponse().getDeclinedUrl();//Campo declinedUrl de Merchant Config
            String cancelUrl = response.getMerchantConfigResponse().getErrorUrl();//Campo errorUrl de Merchant Config
            String useCustomField1 = "0";  //Se mantiene asi por defecto
            String customField1Label = "";//Se mantiene asi por defecto
            String customField1Value = "";//Se mantiene asi por defecto
            String useCustomField2 = "0";//Se mantiene asi por defecto
            String customField2Label = "";//Se mantiene asi por defecto
            String customField2Value = "";//Se mantiene asi por defecto

            // Construir el hash de autenticación
            String data = String.join("", merchantId, merchantName, merchantType, currencyCode, orderNumber, amount, itbis, approvedUrl, declinedUrl, cancelUrl,
                    useCustomField1, customField1Label, customField1Value, useCustomField2, customField2Label, customField2Value, privateKey);
            String authHash = createAuthHash(data);

            // Generar el formulario HTML
            String htmlForm = "<html lang=\"en\">" +
                    "<head></head>" +
                    "<body>" +
                    "<form action=\"" + response.getMerchantConfigResponse().getUrl() + "\" method=\"post\" id=\"paymentForm\">" +
                    "<input type=\"hidden\" name=\"MerchantId\" value=\"" + response.getMerchantConfigResponse().getMerchantNumber() + "\">" +
                    "<input type=\"hidden\" name=\"MerchantName\" value=\"" + response.getMerchantConfigResponse().getName() + "\">" +
                    "<input type=\"hidden\" name=\"MerchantType\" value=\"" + response.getMerchantConfigResponse().getMerchantType() + "\">" +
                    "<input type=\"hidden\" name=\"CurrencyCode\" value=\"" + currencyCode + "\">" +
                    "<input type=\"hidden\" name=\"OrderNumber\" value=\"" + orderNumber + "\">" +
                    "<input type=\"hidden\" name=\"Amount\" value=\"" + amount + "\">" +
                    "<input type=\"hidden\" name=\"ITBIS\" value=\"" + itbis + "\">" +
                    "<input type=\"hidden\" name=\"ApprovedUrl\" value=\"" + response.getMerchantConfigResponse().getSuccessUrl() + "\">" +
                    "<input type=\"hidden\" name=\"DeclinedUrl\" value=\"" + response.getMerchantConfigResponse().getDeclinedUrl() + "\">" +
                    "<input type=\"hidden\" name=\"CancelUrl\" value=\"" + response.getMerchantConfigResponse().getErrorUrl() + "\">" +
                    "<input type=\"hidden\" name=\"UseCustomField1\" value=\"" + useCustomField1 + "\">" +
                    "<input type=\"hidden\" name=\"CustomField1Label\" value=\"" + customField1Label + "\">" +
                    "<input type=\"hidden\" name=\"CustomField1Value\" value=\"" + customField1Value + "\">" +
                    "<input type=\"hidden\" name=\"UseCustomField2\" value=\"" + useCustomField2 + "\">" +
                    "<input type=\"hidden\" name=\"CustomField2Label\" value=\"" + customField2Label + "\">" +
                    "<input type=\"hidden\" name=\"CustomField2Value\" value=\"" + customField2Value + "\">" +
                    "<input type=\"hidden\" name=\"AuthHash\" value=\"" + authHash + "\">" +

                    "</form>" +
                    "<script>document.getElementById('paymentForm').submit();</script>" +
                    "</body>" +
                    "</html>";

            // Devolver el formulario HTML como respuesta se le concatena un json de 1 elemento para que cumpla el formato del split del command
            String concatenatedBody = htmlForm + "{elemento}";
            return ResponseEntity.ok()
                    .contentType(MediaType.TEXT_HTML)
                    .body(concatenatedBody);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing payment.");
        }
    }

    // Método para crear el AuthHash
    private String createAuthHash(String data) {
        try {
            javax.crypto.Mac mac = javax.crypto.Mac.getInstance("HmacSHA512");
            javax.crypto.spec.SecretKeySpec secretKey = new javax.crypto.spec.SecretKeySpec(privateKey.getBytes(), "HmacSHA512");
            mac.init(secretKey);
            byte[] hashBytes = mac.doFinal(data.getBytes());
            StringBuilder hashString = new StringBuilder();
            for (byte b : hashBytes) {
                hashString.append(String.format("%02x", b));
            }
            return hashString.toString();
        } catch (Exception e) {
            throw new RuntimeException("Error generating AuthHash", e);
        }
    }

    public ResponseEntity<String> redirectToCardNetMerchant(ManageMerchantResponse response, PaymentRequestDto requestDto) {
        try {
            // Paso 1: Enviar los datos para generar la sesión
            //TODO: aquí la idea es que la info del merchant se tome de merchant, b2bparter y merchantConfig
            Map<String, String> requestData = new HashMap<>();
            requestData.put("TransactionType", "0200"); // dejar 0200 por defecto por ahora
            requestData.put("CurrencyCode", "214"); // dejar 214 por ahora que es el peso dominicano. El usd es 840
            requestData.put("Tax", "0"); // 0 por defecto
            requestData.put("AcquiringInstitutionCode", response.getMerchantConfigResponse().getInstitutionCode()); //Campo institutionCode de Merchant Config
            requestData.put("MerchantType", response.getMerchantConfigResponse().getMerchantType()); //Campo merchantType de Merchant Config
            requestData.put("MerchantNumber", response.getMerchantConfigResponse().getMerchantNumber()); //Campo merchantNumber de Merchant Config
            requestData.put("MerchantTerminal", response.getMerchantConfigResponse().getMerchantTerminal()); //Campo merchantTerminal de Merchant Config
//            requestData.put("MerchantTerminal_amex", paymentRequest.getMerchantTerminalAmex()); //No enviar por ahora
            requestData.put("ReturnUrl", response.getMerchantConfigResponse().getSuccessUrl()); //Campo successUrl de Merchant Config
            requestData.put("CancelUrl", response.getMerchantConfigResponse().getErrorUrl()); //Campo errorUrl de Merchant Config
            requestData.put("PageLanguaje", "ENG"); //Se envia por ahora ENG
            requestData.put("TransactionId", String.valueOf(requestDto.getTransactionId())); //Viene en el request
            requestData.put("OrdenId", requestDto.getOrderNumber()); //Viene en el request
            requestData.put("MerchantName", response.getMerchantConfigResponse().getName()); //Campo name de Merchant Config
            requestData.put("IpClient", ""); // Campo ip del b2b partner del merchant
            requestData.put("Amount", requestDto.getAmount()); //Viene en el request

            // Enviar la solicitud POST y obtener la respuesta
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, String>> entity = new HttpEntity<>(requestData, headers);

            ResponseEntity<CardNetSessionResponse> sessionResponse = restTemplate.exchange(response.getMerchantConfigResponse().getAltUrl(), HttpMethod.POST, entity, CardNetSessionResponse.class);

            // Convertir la respuesta en objeto
            CardNetSessionResponse sessionData = sessionResponse.getBody();
            if (sessionData == null || sessionData.getSession() == null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al generar la sesión.");
            }

            // Paso 2: Generar Formulario
            String htmlForm = "<html lang=\"en\">" +
                    "<head></head>" +
                    "<body>" +
                    "<form action=\"" + response.getMerchantConfigResponse().getUrl() + "\" method=\"post\" id=\"paymentForm\">" +
                    "<input type=\"hidden\" name=\"SESSION\" value=\"" + sessionData.getSession() + "\"/>" +
                    "<input type=\"hidden\" name=\"ReturnUrl\" value=\"" + response.getMerchantConfigResponse().getSuccessUrl() + "\"/>" +
                    "<input type=\"hidden\" name=\"CancelUrl\" value=\"" + response.getMerchantConfigResponse().getErrorUrl() + "\"/>" +
                    "</form>" +
                    "<script>document.getElementById('paymentForm').submit();</script>" +
                    "</body>" +
                    "</html>";

            String concatenatedBody = htmlForm + requestData;
            return ResponseEntity.ok()
                    .contentType(MediaType.TEXT_HTML)
                    .body(concatenatedBody);



        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing Cardnet payment.");
        }
    }



}

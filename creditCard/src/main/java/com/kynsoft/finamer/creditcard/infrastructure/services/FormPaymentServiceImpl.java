package com.kynsoft.finamer.creditcard.infrastructure.services;

import com.kynsof.share.core.domain.exception.BusinessException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsoft.finamer.creditcard.application.query.objectResponse.CardNetSessionResponse;
import com.kynsoft.finamer.creditcard.domain.dto.*;
import com.kynsoft.finamer.creditcard.domain.dtoEnum.Method;
import com.kynsoft.finamer.creditcard.domain.dtoEnum.MethodType;
import com.kynsoft.finamer.creditcard.domain.services.ICardNetJobService;
import com.kynsoft.finamer.creditcard.domain.services.IFormPaymentService;
import com.kynsoft.finamer.creditcard.domain.services.IMerchantLanguageCodeService;
import com.kynsoft.finamer.creditcard.infrastructure.identity.Transaction;
import com.kynsoft.finamer.creditcard.infrastructure.identity.TransactionPaymentLogs;
import com.kynsoft.finamer.creditcard.infrastructure.repository.command.ManageTransactionsRedirectLogsWriteDataJPARepository;
import com.kynsoft.finamer.creditcard.infrastructure.repository.query.TransactionPaymentLogsReadDataJPARepository;
import com.kynsoft.finamer.creditcard.infrastructure.repository.query.TransactionReadDataJPARepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class FormPaymentServiceImpl implements IFormPaymentService {
    @Value("${redirect.private.key}")
    private String privateKey;

    @Autowired
    private final ManageTransactionsRedirectLogsWriteDataJPARepository repositoryCommand;
    private final TransactionPaymentLogsReadDataJPARepository repositoryQuery;

    private final ICardNetJobService cardNetJobService;

    private final IMerchantLanguageCodeService merchantLanguageCodeService;


    public FormPaymentServiceImpl(ManageTransactionsRedirectLogsWriteDataJPARepository repositoryCommand,
                                  TransactionPaymentLogsReadDataJPARepository repositoryQuery,
                                  ICardNetJobService cardNetJobService, IMerchantLanguageCodeService merchantLanguageCodeService) {
        this.repositoryCommand = repositoryCommand;
        this.repositoryQuery = repositoryQuery;
        this.cardNetJobService = cardNetJobService;
        this.merchantLanguageCodeService = merchantLanguageCodeService;
    }

    public MerchantRedirectResponse redirectToMerchant(TransactionDto transactionDto, ManagerMerchantConfigDto merchantConfigDto) {
        if (transactionDto.getMethodType() == MethodType.LINK && !isValidLink(transactionDto)) {
            throw new BusinessException(DomainErrorMessage.VCC_EXPIRED_PAYMENT_LINK, DomainErrorMessage.VCC_EXPIRED_PAYMENT_LINK.getReasonPhrase());
        } else {
            if (merchantConfigDto.getMethod() == Method.AZUL) {
                return redirectToAzul(transactionDto, merchantConfigDto);
            } else {
                return redirectToCardNet(transactionDto, merchantConfigDto);
            }
        }
    }

    private MerchantRedirectResponse redirectToAzul(TransactionDto transactionDto, ManagerMerchantConfigDto merchantConfigDto) {
        // Extraer los parámetros del objeto PaymentRequest
        //TODO: aquí la idea es que la info del merchant se tome de merchant, b2bparter y merchantConfig
        String merchantId = merchantConfigDto.getMerchantNumber(); //Campo merchantNumber de Merchant Config
        String merchantName = merchantConfigDto.getName(); //Campo name de Merchant Config
        String merchantType = merchantConfigDto.getMerchantType(); //Campo merchantType de Merchant Config
        String currencyCode = transactionDto.getMerchantCurrency().getValue(); //Valor $ por ahora
        String orderNumber = transactionDto.getId().toString(); //Viene en el request

        double amountValue = transactionDto.getAmount() * 100;
        int intValue = (int) Math.round(amountValue);
        String amount = Integer.toString(intValue);

        String itbis = "000"; //Valor 000 por defecto
        String approvedUrl = merchantConfigDto.getSuccessUrl(); //Campo successUrl de Merchant Config
        String declinedUrl = merchantConfigDto.getDeclinedUrl();//Campo declinedUrl de Merchant Config
        String cancelUrl = merchantConfigDto.getErrorUrl();//Campo errorUrl de Merchant Config
        String useCustomField1 = "1";  //Se mantiene asi por defecto
        String customField1Label = "ReferenceNumber";//Se mantiene asi por defecto
        String customField1Value = transactionDto.getReferenceNumber().length() > 200 ? transactionDto.getReferenceNumber().substring(0, 200) : transactionDto.getReferenceNumber();
        String useCustomField2 = "0";//Se mantiene asi por defecto
        String customField2Label = "";//Se mantiene asi por defecto
        String customField2Value = "";//Se mantiene asi por defecto

        // Construir el hash de autenticación
        String data = String.join("", merchantId, merchantName, merchantType, currencyCode, orderNumber, amount, itbis, approvedUrl, declinedUrl, cancelUrl,
                useCustomField1, customField1Label, customField1Value, useCustomField2, customField2Label, customField2Value, privateKey);
        String authHash = createAuthHash(data);

        //obtener el language
        String locale = this.merchantLanguageCodeService.findMerchantLanguageByMerchantIdAndLanguageId(
                transactionDto.getMerchant().getId(),
                transactionDto.getLanguage().getId()
        );

        // Generar el formulario HTML
        String htmlForm = "<html lang=\"en\">" +
                "<head></head>" +
                "<body>" +
                "<form action=\"" + merchantConfigDto.getUrl() + "\" method=\"post\" id=\"paymentForm\">" +
                "<input type=\"hidden\" name=\"MerchantId\" value=\"" + merchantConfigDto.getMerchantNumber() + "\">" +
                "<input type=\"hidden\" name=\"MerchantName\" value=\"" + merchantConfigDto.getName() + "\">" +
                "<input type=\"hidden\" name=\"MerchantType\" value=\"" + merchantConfigDto.getMerchantType() + "\">" +
                "<input type=\"hidden\" name=\"CurrencyCode\" value=\"" + currencyCode + "\">" +
                "<input type=\"hidden\" name=\"OrderNumber\" value=\"" + orderNumber + "\">" +
                "<input type=\"hidden\" name=\"Locale\" value=\"" + (locale.isBlank() ? "EN" : locale) + "\">" +
                "<input type=\"hidden\" name=\"Amount\" value=\"" + amount + "\">" +
                "<input type=\"hidden\" name=\"ITBIS\" value=\"" + itbis + "\">" +
                "<input type=\"hidden\" name=\"ApprovedUrl\" value=\"" + approvedUrl + "\">" +
                "<input type=\"hidden\" name=\"DeclinedUrl\" value=\"" + declinedUrl + "\">" +
                "<input type=\"hidden\" name=\"CancelUrl\" value=\"" + cancelUrl + "\">" +
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

        // Devolver el formulario HTML como respuesta
        return new MerchantRedirectResponse(htmlForm, htmlForm);
    }

    // Métado para crear el AuthHash
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

    private MerchantRedirectResponse redirectToCardNet(TransactionDto transactionDto,
                                                       ManagerMerchantConfigDto merchantConfigDto) {
        // Construir los datos de la transacción
        Map<String, String> requestData = buildRequestData(transactionDto, merchantConfigDto);

        // Obtener o crear una sesión de CardNet
        CardNetSessionResponse sessionResponse = createOrUpdateSession(transactionDto, merchantConfigDto, requestData);

        if (sessionResponse == null || sessionResponse.getSession() == null) {
            throw new BusinessException(DomainErrorMessage.MANAGE_TRANSACTION_CARD_NET_SESSION_ERROR,
                    DomainErrorMessage.MANAGE_TRANSACTION_CARD_NET_SESSION_ERROR.getReasonPhrase());
        }

        // Generar el formulario HTML de redirección
        String htmlForm = generateHtmlForm(merchantConfigDto.getUrl(), sessionResponse.getSession(),
                merchantConfigDto.getSuccessUrl(), merchantConfigDto.getErrorUrl());

        return new MerchantRedirectResponse(htmlForm, requestData.toString());
    }

    /**
     * Construye los datos de la transacción para enviarlos a CardNet.
     */
    private Map<String, String> buildRequestData(TransactionDto transactionDto, ManagerMerchantConfigDto merchantConfigDto) {
        String amountString = BigDecimal.valueOf(transactionDto.getAmount())
                .multiply(BigDecimal.valueOf(100))
                .stripTrailingZeros()
                .toPlainString();

        String pageLanguage = merchantLanguageCodeService.findMerchantLanguageByMerchantIdAndLanguageId(
                transactionDto.getMerchant().getId(),
                transactionDto.getLanguage().getId()
        );

        String referenceNumber = Optional.ofNullable(transactionDto.getReferenceNumber())
                .filter(ref -> ref.length() > 40)
                .map(ref -> ref.substring(0, 40))
                .orElse(transactionDto.getReferenceNumber());

        Map<String, String> requestData = new HashMap<>();
        requestData.put("TransactionType", "0200");
        requestData.put("CurrencyCode", transactionDto.getMerchantCurrency().getValue());
        requestData.put("Tax", "0");
        requestData.put("AcquiringInstitutionCode", merchantConfigDto.getInstitutionCode());
        requestData.put("MerchantType", merchantConfigDto.getMerchantType());
        requestData.put("MerchantNumber", merchantConfigDto.getMerchantNumber());
        requestData.put("MerchantTerminal", merchantConfigDto.getMerchantTerminal());
        requestData.put("ReturnUrl", merchantConfigDto.getSuccessUrl());
        requestData.put("CancelUrl", merchantConfigDto.getErrorUrl());
        requestData.put("PageLanguaje", pageLanguage.isBlank() ? "ING" : pageLanguage);
        requestData.put("TransactionId", referenceNumber);
        requestData.put("OrdenId", transactionDto.getId().toString());
        requestData.put("MerchantName", merchantConfigDto.getName());
        requestData.put("IpClient", "");
        requestData.put("Amount", amountString);
//        String keyEncryptionKey = generateMD5(
//                merchantConfigDto.getMerchantType(),
//                merchantConfigDto.getMerchantNumber(),
//                merchantConfigDto.getMerchantTerminal(),
//                referenceNumber,
//                amountString,
//                "0"
//        );
//        requestData.put("KeyEncriptionKey", keyEncryptionKey);

        return requestData;
    }

    /**
     * Genera un hash MD5 con los valores de los campos requeridos.
     */
    private String generateMD5(String... values) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            String concatenatedString = String.join("", values);
            byte[] digest = md.digest(concatenatedString.getBytes(StandardCharsets.UTF_8));

            // Convertir los bytes en una representación hexadecimal
            StringBuilder hexString = new StringBuilder();
            for (byte b : digest) {
                hexString.append(String.format("%02x", b));
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error generando MD5: " + e.getMessage(), e);
        }
    }

    /**
     * Maneja la obtención o actualización de la sesión de CardNet.
     */
    private CardNetSessionResponse createOrUpdateSession(TransactionDto transactionDto,
                                                         ManagerMerchantConfigDto merchantConfigDto,
                                                         Map<String, String> requestData) {
        CardnetJobDto cardnetJobDto = cardNetJobService.findByTransactionId(transactionDto.getTransactionUuid());

        if (cardnetJobDto == null || (transactionDto.getStatus().isDeclinedStatus() && cardnetJobDto.getIsProcessed()) || cardnetJobDto.isSessionExpired()) {
            // Obtener una nueva sesión
            CardNetSessionResponse sessionResponse = getCardNetSession(requestData, merchantConfigDto.getAltUrl());

            if (sessionResponse != null && sessionResponse.getSession() != null) {
                if (cardnetJobDto == null) {
                    createCardNetJob(sessionResponse.getSession(), sessionResponse.getSessionKey(), transactionDto.getTransactionUuid());
                } else {
                    cardnetJobDto.setIsProcessed(true);
                    cardNetJobService.update(cardnetJobDto);
                    createCardNetJob(sessionResponse.getSession(), sessionResponse.getSessionKey(), transactionDto.getTransactionUuid());
                }
                return sessionResponse;
            }
        } else {
            // Reutilizar la sesión existente si es válida
            cardnetJobDto.setIsProcessed(false);
            cardnetJobDto.setNumberOfAttempts(0);
            cardNetJobService.update(cardnetJobDto);
            return new CardNetSessionResponse(cardnetJobDto.getSession(), cardnetJobDto.getSessionKey());
        }

        return null;
    }

    /**
     * Genera el formulario HTML para la redirección a CardNet.
     */
    private String generateHtmlForm(String actionUrl, String session, String successUrl, String cancelUrl) {
        return String.format("""
        <html lang="en">
        <head></head>
        <body>
            <form action="%s" method="post" id="paymentForm">
                <input type="hidden" name="SESSION" value="%s"/>
                <input type="hidden" name="ReturnUrl" value="%s"/>
                <input type="hidden" name="CancelUrl" value="%s"/>
            </form>
            <script>document.getElementById('paymentForm').submit();</script>
        </body>
        </html>
        """, actionUrl, session, successUrl, cancelUrl);
    }

    private Boolean isValidLink(TransactionDto transaction) {
        LocalDateTime date1 = transaction.getTransactionDate();
        LocalDateTime currentDate = LocalDateTime.now();
        // Calcular la diferencia en minutos
        Duration difernce = Duration.between(date1, currentDate);
        //Comprobar que la diferncia sea menor que una semana
        return difernce.toDays() <= 7;
    }

    private CardNetSessionResponse getCardNetSession(Map<String, String> requestData, String url) {
        // Enviar la solicitud POST y obtener la respuesta
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, String>> entity = new HttpEntity<>(requestData, headers);

        ResponseEntity<CardNetSessionResponse> sessionResponse = restTemplate.exchange(url, HttpMethod.POST, entity, CardNetSessionResponse.class);

        // Convertir la respuesta en objeto
        return sessionResponse.getBody();
    }

    public UUID create(TransactionPaymentLogsDto dto) {
        TransactionPaymentLogs entity = new TransactionPaymentLogs(dto);
        return this.repositoryCommand.save(entity).getId();
    }

    public void update(TransactionPaymentLogsDto dto) {
        TransactionPaymentLogs entity = new TransactionPaymentLogs(dto);
        entity.setUpdatedAt(LocalDateTime.now());
        repositoryCommand.save(entity);
    }

    public TransactionPaymentLogsDto findByTransactionId(UUID id) {
        Optional<TransactionPaymentLogs> optional = this.repositoryQuery.findByTransactionId(id);
        if (optional.isPresent()) {
            return optional.get().toAggregate();
        } else return null;

    }

    private void createCardNetJob(String cardNetSession, String cardNetSessionKey, UUID transactionUuid) {
        // Insertar nueva referencia de session.
        CardnetJobDto cardnetJobDto = new CardnetJobDto(UUID.randomUUID(), transactionUuid, cardNetSession, cardNetSessionKey, Boolean.FALSE, 0);
        cardnetJobDto.setCreatedAt(LocalDateTime.now());
        cardNetJobService.create(cardnetJobDto);
    }

}
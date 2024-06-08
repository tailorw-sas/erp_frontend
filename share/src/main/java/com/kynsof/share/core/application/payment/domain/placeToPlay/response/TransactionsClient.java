package com.kynsof.share.core.application.payment.domain.placeToPlay.response;

import com.kynsof.share.core.application.payment.domain.placeToPlay.request.AmountT;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TransactionsClient {
    private String civilRegistrationId;
    private String channel;
    private String transactionCode;
    private String clientId;
    private String reference;
    private int internalReference;
    private int reversedInternalReference;
    private int requestId;
    private String currentStatus;
    private String expiration;
    private String sendDate;
    private String processUrl;
    private AmountT amount;
    private String type;
    private List<?> processorFields;
    private String id;
    private String createdAt;
    private String updatedAt;


}

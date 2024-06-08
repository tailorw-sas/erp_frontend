package com.kynsof.share.core.application.payment.domain.placeToPlay.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Transactions {
    private String civilRegistrationId;
    private String transactionCode;
    private String currentStatus;
    private String reference;
    private String requestId;
    private String message;
    private boolean error;
}

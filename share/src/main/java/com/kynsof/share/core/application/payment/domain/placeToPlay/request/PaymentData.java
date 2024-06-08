package com.kynsof.share.core.application.payment.domain.placeToPlay.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class PaymentData {
    private UUID ClientId;
    private String TransactionCode;
    private String Locale;
    private String Payer;
    private Buyer Buyer;
    private Payment Payment;
    private String Subscription;
    private String PaymentMethod;
    private String Expiration;
    private String ReturnUrl;
    private String CancelUrl;
    private String IpAddress;
    private String UserAgent;
    private boolean SkipResult;
    private boolean NoBuyerFill;

    
}

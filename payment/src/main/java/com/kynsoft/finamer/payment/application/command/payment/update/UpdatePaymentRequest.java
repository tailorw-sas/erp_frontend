package com.kynsoft.finamer.payment.application.command.payment.update;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
public class UpdatePaymentRequest {

    
    private String reference;
    private LocalDate transactionDate;    
    private String remark;
    private UUID paymentSource;
    private UUID paymentStatus;
    private UUID client;
    private UUID agency;
    private UUID hotel;
    private UUID bankAccount;
    private UUID attachmentStatus;
    private UUID employee;
}

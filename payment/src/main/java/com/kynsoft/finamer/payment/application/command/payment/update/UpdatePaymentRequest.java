package com.kynsoft.finamer.payment.application.command.payment.update;

import com.kynsoft.finamer.payment.domain.dtoEnum.Status;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class UpdatePaymentRequest {

    private UUID paymentSource;
    private String reference;
    private LocalDate transactionDate;
    private UUID paymentStatus;
    private UUID client;
    private UUID agency;
    private UUID hotel;
    private UUID bankAccount;
    private UUID attachmentStatus;

    private Double paymentAmount;
    private Double paymentBalance;
    private Double depositAmount;
    private Double depositBalance;
    private Double otherDeductions;
    private Double identified;
    private Double notIdentified;
    private String remark;
    private Status status;
}

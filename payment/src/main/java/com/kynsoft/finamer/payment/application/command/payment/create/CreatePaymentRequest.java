package com.kynsoft.finamer.payment.application.command.payment.create;

import com.kynsoft.finamer.payment.domain.dtoEnum.Status;
import java.time.LocalDate;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreatePaymentRequest {

    private UUID paymentSource;
    private String reference;
    private LocalDate transactionDate;
    private UUID paymentStatus;
    private UUID client;
    private UUID agency;
    private UUID hotel;
    private UUID bankAccount;
    private UUID attachmentStatus;
    private UUID employee;

    private Double paymentAmount;
    private String remark;
    private Status status;
    private List<CreateAttachmentRequest> attachments;
}

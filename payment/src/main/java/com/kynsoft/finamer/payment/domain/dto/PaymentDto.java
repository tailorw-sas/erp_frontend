package com.kynsoft.finamer.payment.domain.dto;

import com.kynsoft.finamer.payment.domain.dtoEnum.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PaymentDto {

    private UUID id;
    private Long paymentId;
    private Status status;
    private ManagePaymentSourceDto paymentSource;
    private String reference;
    private LocalDate transactionDate;
    private ManagePaymentStatusDto paymentStatus;
    private ManageClientDto client;
    private ManageAgencyDto agency;
    private ManageHotelDto hotel;
    private ManageBankAccountDto bankAccount;
    private ManagePaymentAttachmentStatusDto attachmentStatus;

    private Double paymentAmount;
    private Double paymentBalance;
    private Double depositAmount;
    private Double depositBalance;
    private Double otherDeductions;
    private Double identified;
    private Double notIdentified;
    private String remark;
}

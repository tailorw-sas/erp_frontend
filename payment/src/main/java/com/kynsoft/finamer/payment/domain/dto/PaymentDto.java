package com.kynsoft.finamer.payment.domain.dto;

import com.kynsoft.finamer.payment.domain.dtoEnum.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PaymentDto {

    private UUID id;
    private long paymentId;
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

    private double paymentAmount;
    private double paymentBalance;
    private double depositAmount;
    private double depositBalance;
    private double otherDeductions;
    private double identified;
    private double notIdentified;
    private String remark;

    private List<MasterPaymentAttachmentDto> attachments;
    private OffsetDateTime createdAt;
}

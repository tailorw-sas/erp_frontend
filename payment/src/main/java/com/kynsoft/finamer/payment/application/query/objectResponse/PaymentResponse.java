package com.kynsoft.finamer.payment.application.query.objectResponse;

import com.kynsof.share.core.domain.bus.query.IResponse;
import com.kynsoft.finamer.payment.domain.dto.PaymentDto;
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
public class PaymentResponse implements IResponse {

    private UUID id;
    private Long paymentId;
    private ManagePaymentSourceResponse paymentSource;
    private String reference;
    private LocalDate transactionDate;
    private ManagePaymentStatusResponse paymentStatus;
    private ManageClientResponse client;
    private ManageAgencyResponse agency;
    private ManageHotelResponse hotel;
    private ManageBankAccountResponse bankAccount;
    private ManagePaymentAttachmentStatusResponse attachmentStatus;

    private Double paymentAmount;
    private Double paymentBalance;
    private Double depositAmount;
    private Double depositBalance;
    private Double otherDeductions;
    private Double identified;
    private Double notIdentified;
    private String remark;

    public PaymentResponse(PaymentDto dto) {
        this.id = dto.getId();
        this.paymentId = dto.getPaymentId();
        this.paymentSource = new ManagePaymentSourceResponse(dto.getPaymentSource());
        this.reference = dto.getReference();
        this.transactionDate = dto.getTransactionDate();
        this.paymentStatus = new ManagePaymentStatusResponse(dto.getPaymentStatus());
        this.client = new ManageClientResponse(dto.getClient());
        this.agency = new ManageAgencyResponse(dto.getAgency());
        this.hotel = new ManageHotelResponse(dto.getHotel());
        this.bankAccount = new ManageBankAccountResponse(dto.getBankAccount());
        this.attachmentStatus = new ManagePaymentAttachmentStatusResponse(dto.getAttachmentStatus());
        this.paymentAmount = dto.getPaymentAmount();
        this.paymentBalance = dto.getPaymentBalance();
        this.depositAmount = dto.getDepositAmount();
        this.depositBalance = dto.getDepositBalance();
        this.otherDeductions = dto.getOtherDeductions();
        this.identified = dto.getIdentified();
        this.notIdentified = dto.getNotIdentified();
        this.remark = dto.getRemark();
    }

}

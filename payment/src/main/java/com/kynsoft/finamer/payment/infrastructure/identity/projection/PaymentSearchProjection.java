package com.kynsoft.finamer.payment.infrastructure.identity.projection;

import com.kynsoft.finamer.payment.domain.dtoEnum.EAttachment;
import lombok.Getter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
public class PaymentSearchProjection {

    private UUID id;
    private Long paymentId;
    private LocalDate transactionDate;
    private String reference;
    private PaymentStatusProjection paymentStatus;
    private PaymentSourceProjection paymentSource;
    private AgencyProjection agency;
    private BankAccountProjection bankAccount;
    private ClientProjection client;
    private HotelProjection hotel;
    private PaymentAttachmentStatusProjection paymentAttachmentStatus;

    private Double paymentAmount;
    private Double paymentBalance;
    private Double depositAmount;
    private Double depositBalance;
    private Double otherDeductions;
    private Double identified;
    private Double notIdentified;
    private Double notApplied;
    private Double applied;
    private String remark;
    private EAttachment eAttachment;
    private Boolean applyPayment;
    private Boolean paymentSupport;
    private Boolean createByCredit;
    private boolean hasAttachment;
    private boolean hasDetailTypeDeposit;

    // Constructor Manual
    public PaymentSearchProjection(
            UUID id,
            Long paymentId,
            LocalDate transactionDate,
            String reference,
            PaymentStatusProjection paymentStatus,
            PaymentSourceProjection paymentSource,
            AgencyProjection agency,
            BankAccountProjection bankAccount,
            ClientProjection client,
            HotelProjection hotel,
             PaymentAttachmentStatusProjection paymentAttachmentStatus,
            Double paymentAmount,
            Double paymentBalance,
            Double depositAmount,
            Double depositBalance,
            Double otherDeductions,
            Double identified,
            Double notIdentified,
            Double notApplied,
            Double applied,
            String remark,
            EAttachment eAttachment,
            Boolean applyPayment,
            Boolean paymentSupport,
            Boolean createByCredit,
            Boolean hasAttachment,
            Boolean hasDetailTypeDeposit
    ) {
        this.id = id;
        this.paymentId = paymentId;
        this.transactionDate = transactionDate;
        this.reference = reference;
        this.paymentStatus = paymentStatus;
        this.paymentSource = paymentSource;
        this.agency = agency;
        this.bankAccount = bankAccount;
        this.client = client;
        this.hotel = hotel;
        this.paymentAttachmentStatus = paymentAttachmentStatus;
        this.paymentAmount = paymentAmount;
        this.paymentBalance = paymentBalance;
        this.depositAmount = depositAmount;
        this.depositBalance = depositBalance;
        this.otherDeductions = otherDeductions;
        this.identified = identified;
        this.notIdentified = notIdentified;
        this.notApplied = notApplied;
        this.applied = applied;
        this.remark = remark;
        this.eAttachment = eAttachment;
        this.applyPayment = applyPayment;
        this.paymentSupport = paymentSupport;
        this.createByCredit = createByCredit;
        this.hasAttachment = hasAttachment;
        this.hasDetailTypeDeposit = hasDetailTypeDeposit;
    }

    // Métodos Setter manuales
    public void setId(UUID id) {
        this.id = id;
    }

    public void setPaymentId(Long paymentId) {
        this.paymentId = paymentId;
    }

    public void setTransactionDate(LocalDate transactionDate) {
        this.transactionDate = transactionDate;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public void setPaymentStatus(PaymentStatusProjection paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public void setPaymentSource(PaymentSourceProjection paymentSource) {
        this.paymentSource = paymentSource;
    }

    public void setAgency(AgencyProjection agency) {
        this.agency = agency;
    }

    public void setBankAccount(BankAccountProjection bankAccount) {
        this.bankAccount = bankAccount;
    }

    public void setClient(ClientProjection client) {
        this.client = client;
    }

    public void setHotel(HotelProjection hotel) {
        this.hotel = hotel;
    }

    public void setPaymentAttachmentStatus(PaymentAttachmentStatusProjection paymentAttachmentStatus) {
        this.paymentAttachmentStatus = paymentAttachmentStatus;
    }

    public void setPaymentAmount(Double paymentAmount) {
        this.paymentAmount = paymentAmount;
    }

    public void setPaymentBalance(Double paymentBalance) {
        this.paymentBalance = paymentBalance;
    }

    public void setDepositAmount(Double depositAmount) {
        this.depositAmount = depositAmount;
    }

    public void setDepositBalance(Double depositBalance) {
        this.depositBalance = depositBalance;
    }

    public void setOtherDeductions(Double otherDeductions) {
        this.otherDeductions = otherDeductions;
    }

    public void setIdentified(Double identified) {
        this.identified = identified;
    }

    public void setNotIdentified(Double notIdentified) {
        this.notIdentified = notIdentified;
    }

    public void setNotApplied(Double notApplied) {
        this.notApplied = notApplied;
    }

    public void setApplied(Double applied) {
        this.applied = applied;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public void setEAttachment(EAttachment eAttachment) {
        this.eAttachment = eAttachment;
    }

    public void setApplyPayment(Boolean applyPayment) {
        this.applyPayment = applyPayment;
    }

    public void setPaymentSupport(Boolean paymentSupport) {
        this.paymentSupport = paymentSupport;
    }

    public void setCreateByCredit(Boolean createByCredit) {
        this.createByCredit = createByCredit;
    }

    public void setHasAttachment(Boolean hasAttachment) {
        this.hasAttachment = hasAttachment;
    }
    public void setHasDetailTypeDeposit(Boolean hasDetailTypeDeposit) {
        this.hasDetailTypeDeposit = hasDetailTypeDeposit;
    }

    public Boolean getHasAttachment() {
        return hasAttachment;
    }
}
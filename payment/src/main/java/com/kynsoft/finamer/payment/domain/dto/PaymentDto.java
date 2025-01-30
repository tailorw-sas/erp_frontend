package com.kynsoft.finamer.payment.domain.dto;

import com.kynsoft.finamer.payment.domain.dtoEnum.EAttachment;
import com.kynsoft.finamer.payment.domain.dtoEnum.ImportType;
import com.kynsoft.finamer.payment.domain.dtoEnum.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
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
    private Double notApplied;
    private Double applied;
    private String remark;
    private ManageInvoiceDto invoice;

    private List<MasterPaymentAttachmentDto> attachments;
    private OffsetDateTime createdAt;

    private List<PaymentDetailDto> paymentDetails;
    private EAttachment eAttachment;
    private boolean applyPayment;
    private boolean paymentSupport;
    private boolean createByCredit;
    private LocalTime transactionDateTime;
    private ImportType importType;

    private boolean hasAttachment;
    private boolean hasDetailTypeDeposit;

    public PaymentDto(UUID id, long paymentId, Status status, ManagePaymentSourceDto paymentSource, String reference, LocalDate transactionDate, ManagePaymentStatusDto paymentStatus, ManageClientDto client, ManageAgencyDto agency, ManageHotelDto hotel, ManageBankAccountDto bankAccount, ManagePaymentAttachmentStatusDto attachmentStatus, double paymentAmount, double paymentBalance, double depositAmount, double depositBalance, double otherDeductions, double identified, double notIdentified, Double notApplied, Double applied, String remark, ManageInvoiceDto invoice, List<MasterPaymentAttachmentDto> attachments, OffsetDateTime createdAt, EAttachment eAttachment, LocalTime transactionDateTime) {
        this.id = id;
        this.paymentId = paymentId;
        this.status = status;
        this.paymentSource = paymentSource;
        this.reference = reference;
        this.transactionDate = transactionDate;
        this.paymentStatus = paymentStatus;
        this.client = client;
        this.agency = agency;
        this.hotel = hotel;
        this.bankAccount = bankAccount;
        this.attachmentStatus = attachmentStatus;
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
        this.invoice = invoice;
        this.attachments = attachments;
        this.createdAt = createdAt;
        this.eAttachment = eAttachment;
        this.transactionDateTime = transactionDateTime;
    }

    public PaymentDto(UUID id, Status status, ManagePaymentSourceDto paymentSource, String reference, LocalDate transactionDate, ManagePaymentStatusDto paymentStatus, ManageClientDto client, ManageAgencyDto agency, ManageHotelDto hotel, ManageBankAccountDto bankAccount, ManagePaymentAttachmentStatusDto attachmentStatus, double paymentAmount, double paymentBalance, double depositAmount, double depositBalance, double otherDeductions, double identified, double notIdentified, Double notApplied, Double applied, String remark, ManageInvoiceDto invoice, List<MasterPaymentAttachmentDto> attachments, OffsetDateTime createdAt, EAttachment eAttachment, LocalTime transactionDateTime, long paymentId) {
        this.id = id;
        this.status = status;
        this.paymentSource = paymentSource;
        this.reference = reference;
        this.transactionDate = transactionDate;
        this.paymentStatus = paymentStatus;
        this.client = client;
        this.agency = agency;
        this.hotel = hotel;
        this.bankAccount = bankAccount;
        this.attachmentStatus = attachmentStatus;
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
        this.invoice = invoice;
        this.attachments = attachments;
        this.createdAt = createdAt;
        this.eAttachment = eAttachment;
        this.transactionDateTime = transactionDateTime;
        this.paymentId = paymentId;
    }
}

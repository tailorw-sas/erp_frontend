package com.kynsoft.finamer.payment.infrastructure.identity;

import com.kynsof.audit.infrastructure.core.annotation.RemoteAudit;
import com.kynsof.audit.infrastructure.listener.AuditEntityListener;
import com.kynsof.share.utils.ScaleAmount;
import com.kynsoft.finamer.payment.domain.dto.PaymentDto;
import com.kynsoft.finamer.payment.domain.dto.projection.PaymentProjection;
import com.kynsoft.finamer.payment.domain.dtoEnum.EAttachment;
import com.kynsoft.finamer.payment.domain.dtoEnum.ImportType;
import com.kynsoft.finamer.payment.domain.dtoEnum.Status;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.hibernate.annotations.Generated;
import org.hibernate.generator.EventType;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "payment")
@EntityListeners(AuditEntityListener.class)
@RemoteAudit(name = "payment", id = "7b2ea5e8-e34c-47eb-a811-25a54fe2c604")
public class Payment implements Serializable {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(columnDefinition = "serial", name = "payment_gen_id")
    @Generated(event = EventType.INSERT)
    //@Column(name = "payment_gen_id", nullable = false, updatable = false, unique = true)
    private Long paymentId;

    @Enumerated(EnumType.STRING)
    private Status status;

    //@Column(columnDefinition = "EAttachment DEFAULT NONE")
    @Enumerated(EnumType.STRING)
    private EAttachment eAttachment;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "payment_source_id")
    private ManagePaymentSource paymentSource;
    private String reference;
    private LocalDate transactionDate;

    private LocalTime dateTime;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "payment_status_id")
    private ManagePaymentStatus paymentStatus;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "client_id")
    private ManageClient client;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "agency_id")
    private ManageAgency agency;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "hotel_id")
    private ManageHotel hotel;

    //TODO: este invoice es para relacionar el Payment con el CREDIT padre en el proceso automatico. HU 154
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "invoice_id")
    private Invoice invoice;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "bank_account_id")
    private ManageBankAccount bankAccount;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "attachment_status_id")
    private ManagePaymentAttachmentStatus attachmentStatus;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "resource")
    private List<MasterPaymentAttachment> attachments;

    /**
     * Para obtener la informacion de los invoice, hay que navegar por los
     * detalles del Payment, los booking asociados a el cuando se aplica el
     * pago, son quienes proporcionan la invoice.
     */
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "payment")
    private List<PaymentDetail> paymentDetails;

    //@Column(precision = 2)
    private Double paymentAmount;
    private Double paymentBalance;
    private Double depositAmount;
    private Double depositBalance;
    private Double otherDeductions;
    private Double identified;
    private Double notIdentified;
    private Double notApplied;
    private Double applied;

    @Column(length = 4000)
    private String remark;

    @Column(columnDefinition = "boolean DEFAULT FALSE")
    private boolean applyPayment;

    @Column(columnDefinition = "boolean DEFAULT FALSE")
    private boolean hasAttachment;

    @Column(columnDefinition = "boolean DEFAULT FALSE")
    private boolean hasDetailTypeDeposit;

    @Column(columnDefinition = "boolean DEFAULT FALSE")
    private boolean paymentSupport;

    @Column(columnDefinition = "boolean DEFAULT FALSE")
    private boolean createByCredit;

    //@CreationTimestamp
    @Column(nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(nullable = true, updatable = true)
    private OffsetDateTime updatedAt;

    @Enumerated(EnumType.STRING)
    private ImportType importType;

    @PrePersist
    protected void prePersist() {
        this.createdAt = OffsetDateTime.now(ZoneId.of("UTC"));
    }

    public Payment(PaymentDto dto) {
        this.id = dto.getId();
        //this.paymentId = dto.getPaymentId();
        this.status = dto.getStatus();
        this.paymentSource = dto.getPaymentSource() != null ? new ManagePaymentSource(dto.getPaymentSource()) : null;
        this.paymentStatus = dto.getPaymentStatus() != null ? new ManagePaymentStatus(dto.getPaymentStatus()) : null;
        this.client = dto.getClient() != null ? new ManageClient(dto.getClient()) : null;
        this.agency = dto.getAgency() != null ? new ManageAgency(dto.getAgency()) : null;
        this.hotel = dto.getHotel() != null ? new ManageHotel(dto.getHotel()) : null;
        this.bankAccount = dto.getBankAccount() != null ? new ManageBankAccount(dto.getBankAccount()) : null;
        this.attachmentStatus = dto.getAttachmentStatus() != null ? new ManagePaymentAttachmentStatus(dto.getAttachmentStatus()) : null;
        this.transactionDate = dto.getTransactionDate();
        this.reference = dto.getReference();
        this.paymentAmount = ScaleAmount.scaleAmount(dto.getPaymentAmount());
        this.paymentBalance = ScaleAmount.scaleAmount(dto.getPaymentBalance());
        this.depositAmount = ScaleAmount.scaleAmount(dto.getDepositAmount());
        this.depositBalance = ScaleAmount.scaleAmount(dto.getDepositBalance());
        this.otherDeductions = ScaleAmount.scaleAmount(dto.getOtherDeductions());
        this.identified = ScaleAmount.scaleAmount(dto.getIdentified());
        this.notIdentified = ScaleAmount.scaleAmount(dto.getNotIdentified());
        this.notApplied = ScaleAmount.scaleAmount(dto.getNotApplied());
        this.applied = ScaleAmount.scaleAmount(dto.getApplied());
        this.remark = dto.getRemark();
        this.invoice = dto.getInvoice() != null ? new Invoice(dto.getInvoice()) : null;
        this.eAttachment = dto.getEAttachment();
        this.applyPayment = dto.isApplyPayment();
        this.paymentSupport = dto.isPaymentSupport();
        this.createByCredit = dto.isCreateByCredit();
        this.dateTime = dto.getTransactionDateTime() != null ? dto.getTransactionDateTime() : LocalTime.now();
        this.importType = dto.getImportType() != null ? dto.getImportType() : ImportType.NONE;
        this.hasAttachment = dto.isHasAttachment();
        this.hasDetailTypeDeposit = dto.isHasDetailTypeDeposit();
    }

    public PaymentDto toAggregate() {
        return new PaymentDto(
                id,
                paymentId != null ? paymentId : null,
                status,
                paymentSource != null ? paymentSource.toAggregate() : null,
                reference,
                transactionDate,
                paymentStatus != null ? paymentStatus.toAggregate() : null,
                client != null ? client.toAggregate() : null,
                agency != null ? agency.toAggregate() : null,
                hotel != null ? hotel.toAggregate() : null,
                bankAccount != null ? bankAccount.toAggregate() : null,
                attachmentStatus != null ? attachmentStatus.toAggregate() : null,
                paymentAmount,
                paymentBalance,
                depositAmount,
                depositBalance,
                otherDeductions,
                identified,
                notIdentified,
                notApplied,
                applied,
                remark,
                invoice != null ? invoice.toAggregate() : null,
                attachments != null ? attachments.stream().map(b -> {
                            return b.toAggregateSimple();
                        }).collect(Collectors.toList()) : null,
                createdAt,
                eAttachment != null ? eAttachment : EAttachment.NONE,
                dateTime
        );
    }

    public PaymentDto toAggregateBasicPayment() {
        return new PaymentDto(
                id,
                status,
                paymentSource != null ? paymentSource.toAggregate() : null,
                reference,
                transactionDate,
                paymentStatus != null ? paymentStatus.toAggregate() : null,
                client != null ? client.toAggregate() : null,
                agency != null ? agency.toAggregate() : null,
                hotel != null ? hotel.toAggregate() : null,
                bankAccount != null ? bankAccount.toAggregate() : null,
                attachmentStatus != null ? attachmentStatus.toAggregate() : null,
                paymentAmount,
                paymentBalance,
                depositAmount,
                depositBalance,
                otherDeductions,
                identified,
                notIdentified,
                notApplied,
                applied,
                remark,
                invoice != null ? invoice.toAggregate() : null,
                attachments != null ? attachments.stream().map(b -> {
                            return b.toAggregateSimple();
                        }).collect(Collectors.toList()) : null,
                createdAt,
                eAttachment != null ? eAttachment : EAttachment.NONE,
                dateTime,
                paymentId
        );
    }

    /**
     * Con este metodo se puede obtener un Payment y sus Detalles.
     *
     * @return
     */
    public PaymentDto toAggregateWihtDetails() {
        return new PaymentDto(
                id,
                paymentId != null ? paymentId : null,
                status,
                paymentSource != null ? paymentSource.toAggregate() : null,
                reference,
                transactionDate,
                paymentStatus != null ? paymentStatus.toAggregate() : null,
                client != null ? client.toAggregate() : null,
                agency != null ? agency.toAggregate() : null,
                hotel != null ? hotel.toAggregate() : null,
                bankAccount != null ? bankAccount.toAggregate() : null,
                attachmentStatus != null ? attachmentStatus.toAggregate() : null,
                paymentAmount,
                paymentBalance,
                depositAmount,
                depositBalance,
                otherDeductions,
                identified,
                notIdentified,
                notApplied,
                applied,
                remark,
                invoice != null ? invoice.toAggregateSample() : null,
                attachments != null ? attachments.stream().map(b -> {
                            return b.toAggregateSimple();
                        }).collect(Collectors.toList()) : null,
                createdAt,
                paymentDetails != null ? paymentDetails.stream().map(b -> {
                            return b.toAggregateSimpleNotPayment();
                        }).collect(Collectors.toList()) : null,
                eAttachment != null ? eAttachment : EAttachment.NONE,
                applyPayment,
                paymentSupport,
                createByCredit,
                dateTime,
                importType,
                hasAttachment,
                hasDetailTypeDeposit
        );
    }

}

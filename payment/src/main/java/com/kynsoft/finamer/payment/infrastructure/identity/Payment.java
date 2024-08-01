package com.kynsoft.finamer.payment.infrastructure.identity;

import com.kynsof.share.utils.ScaleAmount;
import com.kynsoft.finamer.payment.domain.dto.PaymentDto;
import com.kynsoft.finamer.payment.domain.dtoEnum.Status;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Generated;
import org.hibernate.generator.EventType;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "payment")
public class Payment implements Serializable {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(columnDefinition="serial", name = "payment_gen_id")
    @Generated(event = EventType.INSERT)
    private Long paymentId;

    @Enumerated(EnumType.STRING)
    private Status status;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "payment_source_id")
    private ManagePaymentSource paymentSource;
    private String reference;
    private LocalDate transactionDate;

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

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "bank_account_id")
    private ManageBankAccount bankAccount;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "attachment_status_id")
    private ManagePaymentAttachmentStatus attachmentStatus;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "resource")
    private List<MasterPaymentAttachment> attachments;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "payment")
    private List<PaymentDetail> paymentDetails;

    @Column(precision = 2)
    private Double paymentAmount;
    private Double paymentBalance;
    private Double depositAmount;
    private Double depositBalance;
    private Double otherDeductions;
    private Double identified;
    private Double notIdentified;
    private String remark;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = true, updatable = true)
    private LocalDateTime updatedAt;

    public Payment(PaymentDto dto) {
        this.id = dto.getId();
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
        this.remark = dto.getRemark();
    }

    public PaymentDto toAggregate(){
        return new PaymentDto(
                id, 
                paymentId,
                status,
                paymentSource.toAggregate() != null ? paymentSource.toAggregate() : null,
                reference, 
                transactionDate, 
                paymentStatus != null ? paymentStatus.toAggregate() : null,
                client != null ? client.toAggregate() : null,
                agency != null ? agency.toAggregate() : null,
                hotel != null ? hotel.toAggregate() : null,
                bankAccount!= null ? bankAccount.toAggregate() : null,
                attachmentStatus != null ? attachmentStatus.toAggregate() : null,
                paymentAmount, 
                paymentBalance, 
                depositAmount, 
                depositBalance, 
                otherDeductions, 
                identified, 
                notIdentified, 
                remark,
                attachments != null ? attachments.stream().map(b -> {
                    return b.toAggregateSimple();
                }).collect(Collectors.toList()) : null
        );
    }

}

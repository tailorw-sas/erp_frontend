package com.kynsoft.finamer.creditcard.infrastructure.identity;

import com.kynsoft.finamer.creditcard.domain.dto.*;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Generated;
import org.hibernate.generator.EventType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "hotel_payment")
public class HotelPayment {

    @Id
    private UUID id;

    @Column(columnDefinition = "serial", name = "hotel_payment_gen_id")
    @Generated(event = EventType.INSERT)
    private Long hotelPaymentId;

    private LocalDateTime transactionDate;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "hotel_id")
    private ManageHotel manageHotel;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "bank_account_id")
    private ManageBankAccount manageBankAccount;

    private double amount;

    private double commission;

    private double netAmount;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "payment_transaction_status_id")
    private ManagePaymentTransactionStatus status;

    private String remark;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "hotelPayment", cascade = CascadeType.MERGE)
    private Set<Transaction> transactions;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "hotelPayment", cascade = CascadeType.MERGE)
    private List<Attachment> attachments;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public HotelPayment(HotelPaymentDto dto){
        this.id = dto.getId();
        this.transactionDate = dto.getTransactionDate();
        this.manageHotel = dto.getManageHotel() != null ? new ManageHotel(dto.getManageHotel()) : null;
        this.manageBankAccount = dto.getManageBankAccount() != null ? new ManageBankAccount(dto.getManageBankAccount()): null;
        this.amount = dto.getAmount();
        this.commission = dto.getCommission();
        this.netAmount = dto.getNetAmount();
        this.status = dto.getStatus() != null ? new ManagePaymentTransactionStatus(dto.getStatus()) : null;
        this.remark = dto.getRemark();
        this.transactions = dto.getTransactions() != null
            ? dto.getTransactions().stream().map(
                t -> {
                    Transaction transaction = new Transaction(t);
                    transaction.setHotelPayment(this);
                    return transaction;
                }).collect(Collectors.toSet())
            : null;
    }

    public HotelPaymentDto toAggregate(){
        return new HotelPaymentDto(
                id, hotelPaymentId, transactionDate,
                manageHotel != null ? manageHotel.toAggregate() : null,
                manageBankAccount != null ? manageBankAccount.toAggregate() : null,
                amount, commission, netAmount,
                status != null ? status.toAggregateSimple() : null,
                remark,
                transactions != null ? transactions.stream().map(Transaction::toAggregate).collect(Collectors.toSet()) : null,
                attachments != null ? attachments.stream().map(Attachment::toAggregate).collect(Collectors.toList()) : null
        );
    }

    public HotelPaymentDto toAggregateSimple(){
        return new HotelPaymentDto(
                id, hotelPaymentId, transactionDate,
                null,
                null,
                amount, commission, netAmount,
                null,
                remark,
                null, null
        );
    }
}

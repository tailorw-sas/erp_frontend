package com.kynsoft.finamer.creditcard.infrastructure.identity;

import com.kynsof.audit.infrastructure.core.annotation.RemoteAudit;
import com.kynsof.audit.infrastructure.listener.AuditEntityListener;
import com.kynsoft.finamer.creditcard.domain.dto.*;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Generated;
import org.hibernate.generator.EventType;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "manage_bank_reconciliation")
@EntityListeners(AuditEntityListener.class)
@RemoteAudit(name = "manage_bank_reconciliation",id="7b2ea5e8-e34c-47eb-a811-25a54fe2c604")
public class ManageBankReconciliation implements Serializable {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(columnDefinition = "serial", name = "reconciliation_gen_id")
    @Generated(event = EventType.INSERT)
    private Long reconciliationId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "merchant_bank_account_id")
    private ManageMerchantBankAccount merchantBankAccount;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "hotel_id")
    private ManageHotel hotel;

    private Double amount;

    private Double detailsAmount;

    private LocalDateTime paidDate;

    private String remark;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "reconcile_status_id")
    private ManageReconcileTransactionStatus reconcileStatus;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "reconciliation", cascade = CascadeType.ALL)
    private Set<Transaction> transactions;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public ManageBankReconciliation(ManageBankReconciliationDto dto){
        this.id = dto.getId();
        this.merchantBankAccount = dto.getMerchantBankAccount() != null ? new ManageMerchantBankAccount(dto.getMerchantBankAccount()) : null;
        this.hotel = dto.getHotel() != null ? new ManageHotel(dto.getHotel()) : null;
        this.amount = dto.getAmount();
        this.detailsAmount = dto.getDetailsAmount();
        this.paidDate = dto.getPaidDate();
        this.remark = dto.getRemark();
        this.reconcileStatus = dto.getReconcileStatus() != null ? new ManageReconcileTransactionStatus(dto.getReconcileStatus()) : null;
        this.transactions = dto.getTransactions() != null
                ? dto.getTransactions().stream().map(
                        t -> {
                            Transaction transaction = new Transaction(t);
                            transaction.setReconciliation(this);
                            return transaction;
                        }).collect(Collectors.toSet())
                : null;
    }

    public ManageBankReconciliationDto toAggregate(){
        return new ManageBankReconciliationDto(
                id,
                reconciliationId,
                merchantBankAccount != null ? merchantBankAccount.toAggregate() : null,
                hotel != null ? hotel.toAggregate() : null,
                amount,
                detailsAmount,
                paidDate,
                remark,
                reconcileStatus != null ? reconcileStatus.toAggregate() : null,
                transactions != null ? transactions.stream().map(Transaction::toAggregate).collect(Collectors.toSet()) : null
        );
    }

    public ManageBankReconciliationDto toAggregateSimple(){
        return new ManageBankReconciliationDto(
                id,
                reconciliationId,
                merchantBankAccount != null ? merchantBankAccount.toAggregateSimple() : null,
                hotel != null ? hotel.toAggregate() : null,
                amount,
                detailsAmount,
                paidDate,
                remark,
                reconcileStatus != null ? reconcileStatus.toAggregate() : null,
               null
        );
    }
}

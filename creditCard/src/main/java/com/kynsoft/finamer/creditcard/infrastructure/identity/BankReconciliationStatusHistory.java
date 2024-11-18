package com.kynsoft.finamer.creditcard.infrastructure.identity;

import com.kynsoft.finamer.creditcard.domain.dto.BankReconciliationStatusHistoryDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "bank_reconciliation_status_history")
public class BankReconciliationStatusHistory {

    @Id
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "bank_reconciliation_id")
    private ManageBankReconciliation bankReconciliation;

    private String description;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private String employee;

    @ManyToOne
    @JoinColumn(name = "reconcile_transaction_status_id")
    private ManageReconcileTransactionStatus reconcileTransactionStatus;

    private LocalDateTime updatedAt;

    public  BankReconciliationStatusHistory(BankReconciliationStatusHistoryDto dto){
        this.id = dto.getId();
        this.bankReconciliation = dto.getBankReconciliation() != null ? new ManageBankReconciliation(dto.getBankReconciliation()) : null;
        this.description = dto.getDescription();
        this.employee = dto.getEmployee();
        this.reconcileTransactionStatus = dto.getReconcileTransactionStatus() != null ? new ManageReconcileTransactionStatus(dto.getReconcileTransactionStatus()) : null;
    }

    public BankReconciliationStatusHistoryDto toAggregate(){
        return new BankReconciliationStatusHistoryDto(
                id,
                bankReconciliation != null ? bankReconciliation.toAggregate() : null,
                description,
                createdAt,
                employee,
                reconcileTransactionStatus != null ? reconcileTransactionStatus.toAggregate() : null
        );
    }
}

package com.kynsoft.finamer.creditcard.infrastructure.identity;

import com.kynsoft.finamer.creditcard.domain.dto.TransactionStatusHistoryDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "transaction_status_history")
public class TransactionStatusHistory implements Serializable {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "transaction_id")
    private Transaction transaction;

    private String description;

    private String employee;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "transaction_status_id")
    private ManageTransactionStatus transactionStatus;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public TransactionStatusHistory(TransactionStatusHistoryDto dto) {
        this.id = dto.getId();
        this.transaction = dto.getTransaction() != null ? new Transaction(dto.getTransaction()) : null;
        this.description = dto.getDescription();
        this.employee = dto.getEmployee();
        this.transactionStatus = dto.getTransactionStatus() != null ? new ManageTransactionStatus(dto.getTransactionStatus()) : null;
    }

    public TransactionStatusHistoryDto toAggregate(){
        return new TransactionStatusHistoryDto(
                id,
                transaction != null ? transaction.toAggregate() : null,
                description,
                createdAt,
                employee,
                transactionStatus != null ? transactionStatus.toAggregate() : null
        );
    }
}

package com.kynsoft.finamer.invoicing.infrastructure.identity;

import com.kynsoft.finamer.invoicing.domain.dto.IncomeAdjustmentDto;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.Status;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
import org.hibernate.annotations.CreationTimestamp;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "income_adjustment")
public class IncomeAdjustment implements Serializable {

    @Id
    @Column(name = "id")
    private UUID id;

    @Enumerated(EnumType.STRING)
    private Status status;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "income_id")
    private Income income;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "transaction_type_id")
    private ManageInvoiceTransactionType transactionType;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "payment_transaction_type_id")
    private ManagePaymentTransactionType paymentTransactionType;

    private Double amount;
    private LocalDate date;
    private String remark;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = true, updatable = true)
    private LocalDateTime updatedAt;

    public IncomeAdjustment(IncomeAdjustmentDto dto) {
        this.id = dto.getId();
        this.income = dto.getIncome() != null ? new Income(dto.getIncome()) : null;
        this.transactionType = dto.getTransactionType() != null ? new ManageInvoiceTransactionType(dto.getTransactionType()) : null;
        this.date = dto.getDate();
        this.amount = dto.getAmount();
        this.remark = dto.getRemark();
        this.status = dto.getStatus();
        this.paymentTransactionType = dto.getPaymentTransactionType() != null ? new ManagePaymentTransactionType(dto.getPaymentTransactionType()): null;
    }

    public IncomeAdjustmentDto toAggregate() {

        return new IncomeAdjustmentDto(
                id,
                status,
                income != null ? income.toAggregate() : null,
                transactionType != null ? transactionType.toAggregate() : null,
                paymentTransactionType != null ? paymentTransactionType.toAggregate() : null,
                amount,
                date,
                remark
        );
    }
}

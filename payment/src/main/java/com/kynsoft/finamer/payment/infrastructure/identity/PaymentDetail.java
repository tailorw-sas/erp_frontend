package com.kynsoft.finamer.payment.infrastructure.identity;

import com.kynsoft.finamer.payment.domain.dto.PaymentDetailDto;
import com.kynsoft.finamer.payment.domain.dtoEnum.Status;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;
import org.hibernate.annotations.CreationTimestamp;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "payment_detail")
public class PaymentDetail implements Serializable {

    @Id
    @Column(name = "id")
    private UUID id;

    @Enumerated(EnumType.STRING)
    private Status status;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "payment_id")
    private Payment payment;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "transaction_type_id")
    private ManagePaymentTransactionType transactionType;

    private Double amount;
    private String remark;

    @ManyToOne(optional = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "fk_pk_payment_detail", nullable = true)
    private PaymentDetail parent;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = true, updatable = true)
    private LocalDateTime updatedAt;

    public PaymentDetail(PaymentDetailDto dto) {
        this.id = dto.getId();
        this.payment = dto.getPayment() != null ? new Payment(dto.getPayment()) : null;
        this.transactionType = dto.getTransactionType() != null ? new ManagePaymentTransactionType(dto.getTransactionType()) : null;
        this.amount = dto.getAmount();
        this.remark = dto.getRemark();
        this.status = dto.getStatus();
        this.parent = dto.getParent() != null ? new PaymentDetail(dto.getParent()) : null;
    }

    public PaymentDetailDto toAggregate() {

        PaymentDetailDto parentDto = null;
        if (this.parent != null) {
            parentDto = new PaymentDetailDto(id, status, payment.toAggregate(), transactionType.toAggregate(), amount, remark, null);
        }

        return new PaymentDetailDto(
                id, 
                status, 
                payment.toAggregate(), 
                transactionType.toAggregate(), 
                amount, 
                remark,
                parentDto
        );
    }
}

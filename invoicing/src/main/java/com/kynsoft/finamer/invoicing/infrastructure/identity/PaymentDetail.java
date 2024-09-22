package com.kynsoft.finamer.invoicing.infrastructure.identity;

import com.kynsoft.finamer.invoicing.domain.dto.PaymentDetailDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "payment_detail")
public class PaymentDetail implements Serializable {

    @Id
    private UUID id;
    private Long paymentDetailId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "payment_id")
    private Payment payment;

    public PaymentDetail(PaymentDetailDto dto) {
        this.id = dto.getId();
        this.paymentDetailId = dto.getPaymentDetailId();
        this.payment = dto.getPayment() != null ? new Payment(dto.getPayment()) : null;
    }

    public PaymentDetailDto toAggregate() {

        return new PaymentDetailDto(
                id,
                paymentDetailId,
                payment.toAggregate()
        );
    }
}

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

    public PaymentDetail(PaymentDetailDto dto) {
        this.id = dto.getId();
        this.paymentDetailId = dto.getPaymentDetailId();
    }

    public PaymentDetailDto toAggregate() {

        return new PaymentDetailDto(
                id,
                paymentDetailId
        );
    }
}

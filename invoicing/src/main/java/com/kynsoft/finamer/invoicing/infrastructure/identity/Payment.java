package com.kynsoft.finamer.invoicing.infrastructure.identity;

import com.kynsoft.finamer.invoicing.domain.dto.PaymentDto;
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
@Table(name = "payment")
public class Payment implements Serializable {

    @Id
    private UUID id;
    private Long paymentId;

    public Payment(PaymentDto dto) {
        this.id = dto.getId();
        this.paymentId = dto.getPaymentId();
    }

    public PaymentDto toAggregate() {
        return new PaymentDto(
                id,
                paymentId
        );
    }

}

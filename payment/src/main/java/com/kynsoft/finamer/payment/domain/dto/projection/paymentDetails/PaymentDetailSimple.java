package com.kynsoft.finamer.payment.domain.dto.projection.paymentDetails;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PaymentDetailSimple implements Serializable {

    private UUID id;
    private Long paymentDetailId;
}

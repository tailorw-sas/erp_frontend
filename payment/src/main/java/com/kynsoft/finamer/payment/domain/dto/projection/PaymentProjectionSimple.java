package com.kynsoft.finamer.payment.domain.dto.projection;

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
public class PaymentProjectionSimple implements Serializable {
    /**
     * Este Objeto sera cacheable, solo se pueden tener aqui datos que no van a cambiar en el tiempo.
     */
    private UUID id;
    private long paymentId;
}

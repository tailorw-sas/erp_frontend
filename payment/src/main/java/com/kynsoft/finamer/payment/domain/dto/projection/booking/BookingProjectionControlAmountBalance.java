package com.kynsoft.finamer.payment.domain.dto.projection.booking;

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
public class BookingProjectionControlAmountBalance implements Serializable {
    private UUID bookingId;
    private Long bookingGenId;
    private double bookingAmountBalance;
    private String couponNumber;
}

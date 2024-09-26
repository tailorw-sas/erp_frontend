package com.kynsoft.finamer.payment.application.command.paymentDetail.applyOtherDeductions;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateApplyOtherDeductionsBookingRequest {

    private UUID bookingId;
    private double bookingBalance;
}

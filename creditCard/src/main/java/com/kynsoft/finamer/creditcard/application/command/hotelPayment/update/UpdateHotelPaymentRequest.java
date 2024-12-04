package com.kynsoft.finamer.creditcard.application.command.hotelPayment.update;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class UpdateHotelPaymentRequest {

    private UUID manageBankAccount;
    private String remark;
    private UUID status;
    private String employee;
}

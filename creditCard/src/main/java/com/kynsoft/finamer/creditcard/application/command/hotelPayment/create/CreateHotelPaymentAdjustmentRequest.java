package com.kynsoft.finamer.creditcard.application.command.hotelPayment.create;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateHotelPaymentAdjustmentRequest {

    private UUID agency;
    private UUID transactionCategory;
    private UUID transactionSubCategory;
    private Double amount;
    private String reservationNumber;
    private String referenceNumber;
}

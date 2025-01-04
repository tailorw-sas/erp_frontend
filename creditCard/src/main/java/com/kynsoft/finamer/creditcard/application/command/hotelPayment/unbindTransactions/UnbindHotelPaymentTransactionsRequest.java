package com.kynsoft.finamer.creditcard.application.command.hotelPayment.unbindTransactions;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class UnbindHotelPaymentTransactionsRequest {

    private UUID hotelPaymentId;
    private List<Long> transactionsIds;
}

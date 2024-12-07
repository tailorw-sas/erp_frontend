package com.kynsoft.finamer.creditcard.application.command.hotelPayment.addTransactions;

import com.kynsoft.finamer.creditcard.application.command.hotelPayment.create.CreateHotelPaymentAdjustmentRequest;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
public class AddHotelPaymentTransactionsRequest {

    private UUID hotelPaymentId;
    private Set<Long> transactionIds;
    private List<CreateHotelPaymentAdjustmentRequest> adjustmentRequests;
    private String employee;
}

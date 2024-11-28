package com.kynsoft.finamer.creditcard.application.command.hotelPayment.create;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
public class CreateHotelPaymentRequest {

    private LocalDateTime transactionDate;
    private UUID manageHotel;
    private UUID manageBankAccount;
    private double amount;
    private double commission;
    private double netAmount;
    private UUID status;
    private String remark;
    private Set<Long> transactions;
    private List<CreateHotelPaymentAdjustmentRequest> adjustmentTransactions;
}

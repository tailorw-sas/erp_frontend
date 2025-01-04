package com.kynsoft.finamer.creditcard.application.command.hotelPayment.addTransactions;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class AddHotelPaymentTransactionsMessage implements ICommandMessage {

    private final String command = "ADD_TRANSACTIONS";
    private UUID hotelPaymentId;
    private Set<Long> transactionIds;
    private List<Long> adjustmentIds;
    private double netAmounts;
    private double commissions;
    private double amounts;
}

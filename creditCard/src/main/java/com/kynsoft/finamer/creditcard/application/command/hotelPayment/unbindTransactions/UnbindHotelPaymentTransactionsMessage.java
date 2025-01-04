package com.kynsoft.finamer.creditcard.application.command.hotelPayment.unbindTransactions;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class UnbindHotelPaymentTransactionsMessage implements ICommandMessage {

    private final String command = "UNBIND_TRANSACTIONS";
    private final UUID hotelPaymentId;
    private final List<Long> transactionsIds;
    private double netAmounts;
    private double amounts;
    private double commissions;
}

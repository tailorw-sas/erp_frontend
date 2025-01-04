package com.kynsoft.finamer.creditcard.application.command.hotelPayment.unbindTransactions;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class UnbindHotelPaymentTransactionsCommand implements ICommand {

    private UUID hotelPaymentId;
    private List<Long> transactionsIds;
    private double netAmounts;
    private double amounts;
    private double commissions;

    public UnbindHotelPaymentTransactionsCommand(UUID hotelPaymentId, List<Long> transactionsIds) {
        this.hotelPaymentId = hotelPaymentId;
        this.transactionsIds = transactionsIds;
    }

    public static UnbindHotelPaymentTransactionsCommand fromRequest(UnbindHotelPaymentTransactionsRequest request) {
        return new UnbindHotelPaymentTransactionsCommand(
                request.getHotelPaymentId(), request.getTransactionsIds()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new UnbindHotelPaymentTransactionsMessage(hotelPaymentId, transactionsIds, netAmounts, amounts, commissions);
    }
}

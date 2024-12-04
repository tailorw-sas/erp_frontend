package com.kynsoft.finamer.creditcard.application.command.hotelPayment.addTransactions;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsoft.finamer.creditcard.application.command.hotelPayment.create.CreateHotelPaymentAdjustmentRequest;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
public class AddHotelPaymentTransactionsCommand implements ICommand {

    private UUID hotelPaymentId;
    private Set<Long> transactionIds;
    private List<CreateHotelPaymentAdjustmentRequest> adjustmentRequests;
    private List<Long> adjustmentIds;
    private double netAmounts;
    private double commissions;
    private double amounts;
    private String employee;

    public AddHotelPaymentTransactionsCommand(UUID hotelPaymentId, Set<Long> transactionIds, List<CreateHotelPaymentAdjustmentRequest> adjustmentRequests, String employee) {
        this.hotelPaymentId = hotelPaymentId;
        this.transactionIds = transactionIds;
        this.adjustmentRequests = adjustmentRequests;
        this.employee = employee;
    }

    public static AddHotelPaymentTransactionsCommand fromRequest(AddHotelPaymentTransactionsRequest request) {
        return new AddHotelPaymentTransactionsCommand(
                request.getHotelPaymentId(), request.getTransactionIds(), request.getAdjustmentRequests(), request.getEmployee()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new AddHotelPaymentTransactionsMessage(hotelPaymentId, transactionIds, adjustmentIds, netAmounts, commissions, amounts);
    }
}

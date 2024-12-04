package com.kynsoft.finamer.creditcard.application.command.hotelPayment.create;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
public class CreateHotelPaymentCommand implements ICommand {

    private UUID id;
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
    private Long hotelPaymentId;
    private String employee;

    public CreateHotelPaymentCommand(LocalDateTime transactionDate, UUID manageHotel, UUID manageBankAccount, double amount, double commission, double netAmount, UUID status, String remark, Set<Long> transactions, List<CreateHotelPaymentAdjustmentRequest> adjustmentTransactions, String employee) {
        this.id = UUID.randomUUID();
        this.transactionDate = transactionDate;
        this.manageHotel = manageHotel;
        this.manageBankAccount = manageBankAccount;
        this.amount = amount;
        this.commission = commission;
        this.netAmount = netAmount;
        this.status = status;
        this.remark = remark;
        this.transactions = transactions;
        this.adjustmentTransactions = adjustmentTransactions;
        this.employee = employee;
    }

    public static CreateHotelPaymentCommand fromRequest(CreateHotelPaymentRequest request) {
        return new CreateHotelPaymentCommand(
                request.getTransactionDate(), request.getManageHotel(), request.getManageBankAccount(),
                request.getAmount(), request.getCommission(), request.getNetAmount(), request.getStatus(),
                request.getRemark(), request.getTransactions(), request.getAdjustmentTransactions(), request.getEmployee()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new CreateHotelPaymentMessage(id, hotelPaymentId);
    }
}

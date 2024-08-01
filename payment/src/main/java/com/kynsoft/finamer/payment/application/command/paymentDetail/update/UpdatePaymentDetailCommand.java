package com.kynsoft.finamer.payment.application.command.paymentDetail.update;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsoft.finamer.payment.domain.dtoEnum.Status;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class UpdatePaymentDetailCommand implements ICommand {
    private UUID id;
    private Status status;
    private UUID employee;
    private UUID payment;
    private UUID transactionType;
    private Double amount;
    private String remark;

    public UpdatePaymentDetailCommand(UUID id, Status status, UUID payment, UUID transactionType, Double amount, String remark, UUID employee) {
        this.id = id;
        this.status = status;
        this.payment = payment;
        this.transactionType = transactionType;
        this.amount = amount;
        this.remark = remark;
        this.employee = employee;
    }

    public static UpdatePaymentDetailCommand fromRequest(UpdatePaymentDetailRequest request, UUID id) {
        return new UpdatePaymentDetailCommand(
                id,
                request.getStatus(),
                request.getPayment(),
                request.getTransactionType(),
                request.getAmount(),
                request.getRemark(),
                request.getEmployee()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new UpdatePaymentDetailMessage(id);
    }
}

package com.kynsoft.finamer.payment.application.command.paymentDetailApplyDeposit.create;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsoft.finamer.payment.domain.dtoEnum.Status;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreatePaymentDetailApplyDepositCommand implements ICommand {

    private UUID id;
    private Status status;
    private UUID paymentDetail;
    private UUID transactionType;
    private Double amount;
    private String remark;

    public CreatePaymentDetailApplyDepositCommand(Status status, UUID paymentDetail, UUID transactionType, Double amount, String remark) {
        this.id = UUID.randomUUID();
        this.status = status;
        this.paymentDetail = paymentDetail;
        this.transactionType = transactionType;
        this.amount = amount;
        this.remark = remark;
    }

    public static CreatePaymentDetailApplyDepositCommand fromRequest(CreatePaymentDetailApplyDepositRequest request) {
        return new CreatePaymentDetailApplyDepositCommand(
                request.getStatus(),
                request.getPaymentDetail(),
                request.getTransactionType(),
                request.getAmount(),
                request.getRemark()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new CreatePaymentDetailApplyDepositMessage(id);
    }
}

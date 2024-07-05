package com.kynsoft.finamer.payment.application.command.paymentDetail.create;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsoft.finamer.payment.domain.dto.PaymentDto;
import com.kynsoft.finamer.payment.domain.dtoEnum.Status;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreatePaymentDetailCommand implements ICommand {

    private UUID id;
    private Status status;
    private UUID payment;
    private UUID transactionType;
    private Double amount;
    private String remark;

    private PaymentDto paymentResponse;

    public CreatePaymentDetailCommand(Status status, UUID payment, UUID transactionType, Double amount, String remark) {
        this.id = UUID.randomUUID();
        this.status = status;
        this.payment = payment;
        this.transactionType = transactionType;
        this.amount = amount;
        this.remark = remark;
    }

    public static CreatePaymentDetailCommand fromRequest(CreatePaymentDetailRequest request) {
        return new CreatePaymentDetailCommand(
                request.getStatus(),
                request.getPayment(),
                request.getTransactionType(),
                request.getAmount(),
                request.getRemark()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new CreatePaymentDetailMessage(paymentResponse);
    }
}

package com.kynsoft.finamer.payment.application.command.paymentDetailSplitDeposit.create;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsoft.finamer.payment.domain.dto.PaymentDto;
import com.kynsoft.finamer.payment.domain.dtoEnum.Status;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreatePaymentDetailSplitDepositCommand implements ICommand {

    private UUID id;
    private Status status;
    private UUID employee;
    private UUID paymentDetail;
    private UUID transactionType;
    private Double amount;
    private String remark;

    private PaymentDto paymentResponse;

    public CreatePaymentDetailSplitDepositCommand(Status status, UUID paymentDetail, UUID transactionType, Double amount, String remark, UUID employee) {
        this.id = UUID.randomUUID();
        this.status = status;
        this.paymentDetail = paymentDetail;
        this.transactionType = transactionType;
        this.amount = amount;
        this.remark = remark;
        this.employee = employee;
    }

    public static CreatePaymentDetailSplitDepositCommand fromRequest(CreatePaymentDetailSplitDepositRequest request) {
        return new CreatePaymentDetailSplitDepositCommand(
                request.getStatus(),
                request.getPaymentDetail(),
                request.getTransactionType(),
                request.getAmount(),
                request.getRemark(),
                request.getEmployee()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new CreatePaymentDetailSplitDepositMessage(paymentResponse);
    }
}

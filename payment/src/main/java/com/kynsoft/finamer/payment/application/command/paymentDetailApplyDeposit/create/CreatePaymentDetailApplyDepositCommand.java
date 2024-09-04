package com.kynsoft.finamer.payment.application.command.paymentDetailApplyDeposit.create;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.payment.domain.dto.PaymentDto;
import com.kynsoft.finamer.payment.domain.dtoEnum.Status;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreatePaymentDetailApplyDepositCommand implements ICommand {

    private UUID id;
    private Status status;
    private UUID employee;
    private UUID paymentDetail;
    private UUID transactionType;
    private UUID transactionTypeForAdjustment;
    private Double amount;
    private String remark;

    private UUID booking;
    private Boolean applyPayment;

    private PaymentDto paymentResponse;
    private final IMediator mediator;

    public CreatePaymentDetailApplyDepositCommand(Status status, UUID paymentDetail, UUID transactionType, Double amount, String remark, UUID employee,UUID transactionTypeForAdjustment, UUID booking, Boolean applyPayment, final IMediator mediator) {
        this.id = UUID.randomUUID();
        this.status = status;
        this.paymentDetail = paymentDetail;
        this.transactionType = transactionType;
        this.amount = amount;
        this.remark = remark;
        this.employee = employee;
        this.transactionTypeForAdjustment =transactionTypeForAdjustment;
        this.booking = booking;
        this.applyPayment = applyPayment;
        this.mediator = mediator;
    }

    public CreatePaymentDetailApplyDepositCommand(Status status, UUID paymentDetail, UUID transactionType, Double amount, String remark, UUID employee,UUID transactionTypeForAdjustment) {
        this.id = UUID.randomUUID();
        this.status = status;
        this.paymentDetail = paymentDetail;
        this.transactionType = transactionType;
        this.amount = amount;
        this.remark = remark;
        this.employee = employee;
        this.transactionTypeForAdjustment =transactionTypeForAdjustment;
        this.mediator = null;
    }

    public static CreatePaymentDetailApplyDepositCommand fromRequest(CreatePaymentDetailApplyDepositRequest request, final IMediator mediator) {
        return new CreatePaymentDetailApplyDepositCommand(
                request.getStatus(),
                request.getPaymentDetail(),
                request.getTransactionType(),
                request.getAmount(),
                request.getRemark(),
                request.getEmployee(),
                request.getTransactionTypeForAdjustment(),
                request.getBooking(),
                request.getApplyPayment(),
                mediator
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new CreatePaymentDetailApplyDepositMessage(paymentResponse);
    }
}

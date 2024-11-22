package com.kynsoft.finamer.payment.application.command.paymentDetail.create;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.payment.application.command.paymentDetail.applyOtherDeductions.CreateApplyOtherDeductionsRequest;
import com.kynsoft.finamer.payment.domain.dto.PaymentDto;
import com.kynsoft.finamer.payment.domain.dtoEnum.Status;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreatePaymentDetailCommand implements ICommand {

    private UUID id;
    private UUID employee;
    private Status status;
    private UUID payment;
    private UUID transactionType;
    private Double amount;
    private String remark;

    private UUID booking;
    private Boolean applyPayment;

    private CreateApplyOtherDeductionsRequest otherDeductions;

    private PaymentDto paymentResponse;
    private final IMediator mediator;

    public CreatePaymentDetailCommand(Status status, UUID payment, UUID transactionType, Double amount, String remark, UUID employee, UUID booking, Boolean applyPayment, final IMediator mediator, CreateApplyOtherDeductionsRequest otherDeductions) {
        this.id = UUID.randomUUID();
        this.status = status;
        this.payment = payment;
        this.transactionType = transactionType;
        this.amount = amount;
        this.remark = remark;
        this.employee = employee;
        this.booking = booking;
        this.applyPayment = applyPayment;
        this.mediator = mediator;
        this.otherDeductions = otherDeductions;
    }

    public static CreatePaymentDetailCommand fromRequest(CreatePaymentDetailRequest request, final IMediator mediator) {
        return new CreatePaymentDetailCommand(
                request.getStatus(),
                request.getPayment(),
                request.getTransactionType(),
                request.getAmount(),
                request.getRemark(),
                request.getEmployee(),
                request.getBooking(),
                request.getApplyPayment(),
                mediator,
                request.getOtherDeductions()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new CreatePaymentDetailMessage(paymentResponse);
    }
}

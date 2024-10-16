package com.kynsoft.finamer.payment.application.command.paymentDetail.createFormImport;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.payment.application.command.paymentDetail.create.CreatePaymentDetailRequest;
import com.kynsoft.finamer.payment.domain.dto.PaymentDto;
import com.kynsoft.finamer.payment.domain.dtoEnum.Status;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreatePaymentDetailFromFileCommand implements ICommand {

    private UUID id;
    private UUID employee;
    private Status status;
    private UUID payment;
    private UUID transactionType;
    private Double amount;
    private String remark;

    private UUID booking;
    private Boolean applyPayment;

    private PaymentDto paymentResponse;
    private final IMediator mediator;

    public CreatePaymentDetailFromFileCommand(Status status, UUID payment, UUID transactionType, Double amount, String remark, UUID employee, UUID booking, Boolean applyPayment, final IMediator mediator) {
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
    }

    public static CreatePaymentDetailFromFileCommand fromRequest(CreatePaymentDetailRequest request, final IMediator mediator) {
        return new CreatePaymentDetailFromFileCommand(
                request.getStatus(),
                request.getPayment(),
                request.getTransactionType(),
                request.getAmount(),
                request.getRemark(),
                request.getEmployee(),
                request.getBooking(),
                request.getApplyPayment(),
                mediator
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new CreatePaymentDetailFromFileMessage(paymentResponse);
    }
}

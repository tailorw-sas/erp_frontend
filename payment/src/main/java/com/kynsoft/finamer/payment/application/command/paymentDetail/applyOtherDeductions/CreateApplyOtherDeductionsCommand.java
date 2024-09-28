package com.kynsoft.finamer.payment.application.command.paymentDetail.applyOtherDeductions;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.payment.domain.dto.PaymentDto;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateApplyOtherDeductionsCommand implements ICommand {

    private UUID payment;
    private UUID transactionType;
    private String remark;

    private List<CreateApplyOtherDeductionsBookingRequest> booking;

    private PaymentDto paymentResponse;
    private final IMediator mediator;

    public CreateApplyOtherDeductionsCommand(UUID payment, UUID transactionType, String remark, List<CreateApplyOtherDeductionsBookingRequest> booking, IMediator mediator) {
        this.payment = payment;
        this.transactionType = transactionType;
        this.remark = remark;
        this.booking = booking;
        this.mediator = mediator;
    }

    public static CreateApplyOtherDeductionsCommand fromRequest(CreateApplyOtherDeductionsRequest request, IMediator mediator) {
        return new CreateApplyOtherDeductionsCommand(
                request.getPayment(),
                request.getTransactionType(),
                request.getRemark(),
                request.getBooking(),
                mediator
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new CreateApplyOtherDeductionsMessage(paymentResponse);
    }
}

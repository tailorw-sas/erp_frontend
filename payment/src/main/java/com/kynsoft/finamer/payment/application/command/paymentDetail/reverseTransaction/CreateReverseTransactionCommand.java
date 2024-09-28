package com.kynsoft.finamer.payment.application.command.paymentDetail.reverseTransaction;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateReverseTransactionCommand implements ICommand {

    private UUID paymentDetail;
    private final IMediator mediator;

    public CreateReverseTransactionCommand(UUID paymentDetail, IMediator mediator) {
        this.paymentDetail = paymentDetail;
        this.mediator = mediator;
    }

    public static CreateReverseTransactionCommand fromRequest(CreateReverseTransactionRequest request, IMediator mediator) {
        return new CreateReverseTransactionCommand(
                request.getPaymentDetail(),
                mediator
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new CreateReverseTransactionMessage(paymentDetail);
    }
}

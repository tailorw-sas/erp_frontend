package com.kynsoft.finamer.payment.infrastructure.excel.event.createPayment;

import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.payment.application.command.paymentDetail.create.CreatePaymentDetailCommand;
import com.kynsoft.finamer.payment.domain.dtoEnum.Status;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class CreatePaymentDetailHandler implements ApplicationListener<CreatePaymentDetailEvent> {

    private final IMediator mediator;

    public CreatePaymentDetailHandler(IMediator mediator) {
        this.mediator = mediator;
    }

    @Override
    public void onApplicationEvent(CreatePaymentDetailEvent event) {
        CreatePaymentDetailCommand createPaymentDetailCommand = new CreatePaymentDetailCommand(Status.ACTIVE,event.getPayment(),
                event.getTransactionType(),event.getAmount(),event.getRemark(),event.getEmployee(), null, false, mediator);
        mediator.send(createPaymentDetailCommand);
    }
}

package com.kynsoft.finamer.payment.application.command.payment.setVariables;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.payment.domain.services.*;
import org.springframework.stereotype.Component;

@Component
public class SetCommandHandler implements ICommandHandler<SetCommand> {

    private final IPaymentService paymentService;

    public SetCommandHandler(IPaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @Override
    public void handle(SetCommand command) {
        this.paymentService.getAll();
    }
}

package com.kynsoft.finamer.payment.application.command.payment.delete;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.payment.domain.dto.PaymentDto;
import com.kynsoft.finamer.payment.domain.services.IPaymentService;
import org.springframework.stereotype.Component;

@Component
public class DeletePaymentCommandHandler implements ICommandHandler<DeletePaymentCommand> {

    private final IPaymentService service;

    public DeletePaymentCommandHandler(IPaymentService service) {
        this.service = service;
    }

    @Override
    public void handle(DeletePaymentCommand command) {
        PaymentDto delete = this.service.findById(command.getId());

        service.delete(delete);
    }

}

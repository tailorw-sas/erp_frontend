package com.kynsoft.finamer.payment.application.command.paymentcloseoperation.delete;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.payment.domain.dto.PaymentCloseOperationDto;
import com.kynsoft.finamer.payment.domain.services.IPaymentCloseOperationService;
import org.springframework.stereotype.Component;

@Component
public class DeletePaymentCloseOperationCommandHandler implements ICommandHandler<DeletePaymentCloseOperationCommand> {

    private final IPaymentCloseOperationService service;

    public DeletePaymentCloseOperationCommandHandler(IPaymentCloseOperationService service) {
        this.service = service;
    }

    @Override
    public void handle(DeletePaymentCloseOperationCommand command) {
        PaymentCloseOperationDto delete = this.service.findById(command.getId());

        service.delete(delete);
    }

}

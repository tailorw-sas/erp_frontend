package com.kynsoft.finamer.payment.application.command.payment.update;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.payment.application.services.payment.update.UpdatePaymentService;
import org.springframework.stereotype.Component;

@Component
public class UpdatePaymentCommandHandler implements ICommandHandler<UpdatePaymentCommand> {

    private final UpdatePaymentService updatePaymentService;

    public UpdatePaymentCommandHandler(UpdatePaymentService updatePaymentService) {
        this.updatePaymentService = updatePaymentService;
    }

    @Override
    public void handle(UpdatePaymentCommand command) {
        this.updatePaymentService.update(command.getId(),
                command.getClient(),
                command.getAgency(),
                command.getHotel(),
                command.getBankAccount(),
                command.getRemark(),
                command.getPaymentStatus());
    }
}

package com.kynsoft.finamer.creditcard.application.command.creditCardCloseOperation.delete;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.creditcard.domain.dto.CreditCardCloseOperationDto;
import com.kynsoft.finamer.creditcard.domain.services.ICreditCardCloseOperationService;
import org.springframework.stereotype.Component;

@Component
public class DeleteCreditCardCloseOperationCommandHandler implements ICommandHandler<DeleteCreditCardCloseOperationCommand> {

    private final ICreditCardCloseOperationService closeOperationService;

    public DeleteCreditCardCloseOperationCommandHandler(ICreditCardCloseOperationService closeOperationService) {
        this.closeOperationService = closeOperationService;
    }

    @Override
    public void handle(DeleteCreditCardCloseOperationCommand command) {
        CreditCardCloseOperationDto delete = this.closeOperationService.findById(command.getId());

        closeOperationService.delete(delete);
    }

}

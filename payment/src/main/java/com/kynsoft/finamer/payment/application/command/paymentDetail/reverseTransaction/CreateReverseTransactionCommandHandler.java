package com.kynsoft.finamer.payment.application.command.paymentDetail.reverseTransaction;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.payment.application.services.paymentDetail.reverse.ReverseTransactionService;
import org.springframework.stereotype.Component;

@Component
public class CreateReverseTransactionCommandHandler implements ICommandHandler<CreateReverseTransactionCommand> {

    private final ReverseTransactionService reverseTransactionService;

    public CreateReverseTransactionCommandHandler(ReverseTransactionService reverseTransactionService) {
        this.reverseTransactionService = reverseTransactionService;
    }

    @Override
    public void handle(CreateReverseTransactionCommand command) {
        this.reverseTransactionService.reverseTransaction(command);
    }
}

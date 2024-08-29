package com.kynsoft.finamer.payment.application.command.managePaymentTransactionType.create;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.payment.domain.dto.ManagePaymentTransactionTypeDto;
import com.kynsoft.finamer.payment.domain.services.IManagePaymentTransactionTypeService;
import org.springframework.stereotype.Component;

@Component
public class CreateManagePaymentTransactionTypeCommandHandler implements ICommandHandler<CreateManagePaymentTransactionTypeCommand> {

    private final IManagePaymentTransactionTypeService service;

    public CreateManagePaymentTransactionTypeCommandHandler(IManagePaymentTransactionTypeService service) {
        this.service = service;
    }

    @Override
    public void handle(CreateManagePaymentTransactionTypeCommand command) {
        service.create(new ManagePaymentTransactionTypeDto(
                command.getId(),
                command.getCode(),
                command.getName(),
                command.getStatus(),
                command.getCash(),
                command.getDeposit(),
                command.getApplyDeposit(),
                command.getRemarkRequired(),
                command.getMinNumberOfCharacter(),
                command.getDefaultRemark(),
                command.isDefaults(),
                command.getPaymentInvoice()
        ));
    }
}

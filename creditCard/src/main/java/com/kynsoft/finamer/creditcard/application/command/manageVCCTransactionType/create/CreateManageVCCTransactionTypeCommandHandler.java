package com.kynsoft.finamer.creditcard.application.command.manageVCCTransactionType.create;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.creditcard.domain.dto.ManageVCCTransactionTypeDto;
import com.kynsoft.finamer.creditcard.domain.services.IManageVCCTransactionTypeService;
import org.springframework.stereotype.Component;

@Component
public class CreateManageVCCTransactionTypeCommandHandler implements ICommandHandler<CreateManageVCCTransactionTypeCommand> {

    private final IManageVCCTransactionTypeService service;

    public CreateManageVCCTransactionTypeCommandHandler(IManageVCCTransactionTypeService service) {
        this.service = service;
    }

    @Override
    public void handle(CreateManageVCCTransactionTypeCommand command) {
        service.create(new ManageVCCTransactionTypeDto(
                command.getId(),
                command.getCode(),
                command.getName(),
                command.getIsDefault(),
                command.getSubcategory()
        ));
    }
}

package com.kynsoft.finamer.creditcard.application.command.manageVCCTransactionType.update;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.creditcard.domain.dto.ManageVCCTransactionTypeDto;
import com.kynsoft.finamer.creditcard.domain.services.IManageVCCTransactionTypeService;
import org.springframework.stereotype.Component;

@Component
public class UpdateManageVCCTransactionTypeCommandHandler implements ICommandHandler<UpdateManageVCCTransactionTypeCommand> {

    private final IManageVCCTransactionTypeService service;

    public UpdateManageVCCTransactionTypeCommandHandler(IManageVCCTransactionTypeService service) {
        this.service = service;
    }

    @Override
    public void handle(UpdateManageVCCTransactionTypeCommand command) {
        ManageVCCTransactionTypeDto dto = this.service.findById(command.getId());
        dto.setName(command.getName());
        this.service.update(dto);
    }

}

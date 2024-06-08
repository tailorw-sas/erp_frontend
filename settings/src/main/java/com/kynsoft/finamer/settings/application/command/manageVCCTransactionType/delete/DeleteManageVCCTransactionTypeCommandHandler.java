package com.kynsoft.finamer.settings.application.command.manageVCCTransactionType.delete;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.settings.domain.dto.ManageVCCTransactionTypeDto;
import com.kynsoft.finamer.settings.domain.services.IManageVCCTransactionTypeService;
import org.springframework.stereotype.Component;

@Component
public class DeleteManageVCCTransactionTypeCommandHandler implements ICommandHandler<DeleteManageVCCTransactionTypeCommand> {

    private final IManageVCCTransactionTypeService service;

    public DeleteManageVCCTransactionTypeCommandHandler(IManageVCCTransactionTypeService service) {
        this.service = service;
    }

    @Override
    public void handle(DeleteManageVCCTransactionTypeCommand command) {
        ManageVCCTransactionTypeDto dto = service.findById(command.getId());

        service.delete(dto);
    }
}

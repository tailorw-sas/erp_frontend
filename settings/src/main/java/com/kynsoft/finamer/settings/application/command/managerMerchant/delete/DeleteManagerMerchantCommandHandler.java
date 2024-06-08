package com.kynsoft.finamer.settings.application.command.managerMerchant.delete;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.settings.domain.dto.ManagerMerchantDto;
import com.kynsoft.finamer.settings.domain.services.IManagerMerchantService;
import org.springframework.stereotype.Component;

@Component
public class DeleteManagerMerchantCommandHandler implements ICommandHandler<DeleteManagerMerchantCommand> {

    private final IManagerMerchantService service;

    public DeleteManagerMerchantCommandHandler(IManagerMerchantService service) {
        this.service = service;
    }

    @Override
    public void handle(DeleteManagerMerchantCommand command) {
        ManagerMerchantDto delete = this.service.findById(command.getId());

        service.delete(delete);
    }

}

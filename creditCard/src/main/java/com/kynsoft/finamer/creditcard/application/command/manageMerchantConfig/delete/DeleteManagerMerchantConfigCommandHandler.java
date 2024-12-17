package com.kynsoft.finamer.creditcard.application.command.manageMerchantConfig.delete;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.creditcard.domain.dto.ManagerMerchantConfigDto;
import com.kynsoft.finamer.creditcard.domain.services.IManageMerchantConfigService;
import org.springframework.stereotype.Component;

@Component
public class DeleteManagerMerchantConfigCommandHandler implements ICommandHandler<DeleteManagerMerchantConfigCommand> {

    private final IManageMerchantConfigService service;

    public DeleteManagerMerchantConfigCommandHandler(IManageMerchantConfigService service) {
        this.service = service;
    }

    @Override
    public void handle(DeleteManagerMerchantConfigCommand command) {
        ManagerMerchantConfigDto delete = this.service.findById(command.getId());
        service.delete(delete);
    }
}

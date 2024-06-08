package com.kynsoft.finamer.settings.application.command.manageMerchantCommission.delete;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.settings.domain.dto.ManageMerchantCommissionDto;
import com.kynsoft.finamer.settings.domain.services.IManageMerchantCommissionService;
import org.springframework.stereotype.Component;

@Component
public class DeleteManageMerchantCommissionCommandHandler implements ICommandHandler<DeleteManageMerchantCommissionCommand> {

    private final IManageMerchantCommissionService service;

    public DeleteManageMerchantCommissionCommandHandler(IManageMerchantCommissionService service) {
        this.service = service;
    }

    @Override
    public void handle(DeleteManageMerchantCommissionCommand command) {
        ManageMerchantCommissionDto delete = this.service.findById(command.getId());

        service.delete(delete);
    }

}

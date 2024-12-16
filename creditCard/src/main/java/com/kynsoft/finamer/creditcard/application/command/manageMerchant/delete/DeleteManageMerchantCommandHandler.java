package com.kynsoft.finamer.creditcard.application.command.manageMerchant.delete;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.creditcard.domain.dto.ManageMerchantDto;
import com.kynsoft.finamer.creditcard.domain.services.IManageMerchantService;
import org.springframework.stereotype.Component;

@Component
public class DeleteManageMerchantCommandHandler implements ICommandHandler<DeleteManageMerchantCommand> {

    private final IManageMerchantService service;

    public DeleteManageMerchantCommandHandler(IManageMerchantService service) {
        this.service = service;
    }

    @Override
    public void handle(DeleteManageMerchantCommand command) {
        ManageMerchantDto delete = this.service.findById(command.getId());

        service.delete(delete);
    }

}

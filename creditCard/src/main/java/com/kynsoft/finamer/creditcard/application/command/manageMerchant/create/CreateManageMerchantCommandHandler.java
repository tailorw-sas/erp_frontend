package com.kynsoft.finamer.creditcard.application.command.manageMerchant.create;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.creditcard.domain.dto.ManageMerchantDto;
import com.kynsoft.finamer.creditcard.domain.services.IManageMerchantService;
import org.springframework.stereotype.Component;

@Component
public class CreateManageMerchantCommandHandler implements ICommandHandler<CreateManageMerchantCommand> {

    private final IManageMerchantService service;

    public CreateManageMerchantCommandHandler(IManageMerchantService service) {
        this.service = service;
    }

    @Override
    public void handle(CreateManageMerchantCommand command) {
        service.create(new ManageMerchantDto(
                command.getId(),
                command.getCode()
        ));
    }
}


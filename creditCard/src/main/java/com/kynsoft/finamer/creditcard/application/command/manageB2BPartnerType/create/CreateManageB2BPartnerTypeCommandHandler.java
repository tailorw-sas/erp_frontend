package com.kynsoft.finamer.creditcard.application.command.manageB2BPartnerType.create;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.creditcard.domain.dto.ManageB2BPartnerTypeDto;
import com.kynsoft.finamer.creditcard.domain.services.IManageB2BPartnerTypeService;
import org.springframework.stereotype.Component;

@Component
public class CreateManageB2BPartnerTypeCommandHandler implements ICommandHandler<CreateManageB2BPartnerTypeCommand> {

    private final IManageB2BPartnerTypeService service;

    public CreateManageB2BPartnerTypeCommandHandler(IManageB2BPartnerTypeService service) {
        this.service = service;
    }

    @Override
    public void handle(CreateManageB2BPartnerTypeCommand command) {

        service.create(new ManageB2BPartnerTypeDto(
                command.getId(),
                command.getCode(),
                command.getName(),
                command.getDescription(),
                command.getStatus()
        ));
    }
}

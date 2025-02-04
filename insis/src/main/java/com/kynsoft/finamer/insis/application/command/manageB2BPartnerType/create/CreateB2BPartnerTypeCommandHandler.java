package com.kynsoft.finamer.insis.application.command.manageB2BPartnerType.create;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.insis.domain.dto.ManageB2BPartnerTypeDto;
import com.kynsoft.finamer.insis.domain.services.IManageB2BPartnerTypeService;
import org.springframework.stereotype.Component;

@Component
public class CreateB2BPartnerTypeCommandHandler implements ICommandHandler<CreateB2BPartnerTypeCommand> {

    private final IManageB2BPartnerTypeService service;

    public CreateB2BPartnerTypeCommandHandler(IManageB2BPartnerTypeService service){
        this.service = service;
    }

    @Override
    public void handle(CreateB2BPartnerTypeCommand command) {
        service.create(new ManageB2BPartnerTypeDto(
                command.getId(),
                command.getCode(),
                command.getName(),
                command.getStatus(),
                command.getUpdatedAt()
        ));
    }
}

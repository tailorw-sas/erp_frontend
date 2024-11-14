package com.kynsoft.finamer.creditcard.application.command.manageB2BPartner.create;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.creditcard.domain.dto.ManageB2BPartnerTypeDto;
import com.kynsoft.finamer.creditcard.domain.services.IManageB2BPartnerTypeService;
import com.kynsoft.finamer.creditcard.domain.services.IManagerB2BPartnerService;
import com.kynsoft.finamer.creditcard.domain.dto.ManagerB2BPartnerDto;
import org.springframework.stereotype.Component;

@Component
public class CreateManagerB2BPartnerCommandHandler implements ICommandHandler<CreateManagerB2BPartnerCommand> {

    private final IManagerB2BPartnerService service;
    private final IManageB2BPartnerTypeService b2BPartnerTypeService;

    public CreateManagerB2BPartnerCommandHandler(IManagerB2BPartnerService service, IManageB2BPartnerTypeService b2BPartnerTypeService) {
        this.service = service;
        this.b2BPartnerTypeService = b2BPartnerTypeService;
    }

    @Override
    public void handle(CreateManagerB2BPartnerCommand command) {
        ManageB2BPartnerTypeDto b2BPartnerTypeDto = b2BPartnerTypeService.findById(command.getManageB2BPartnerType());
        service.create(new ManagerB2BPartnerDto(
                command.getId(), 
                command.getCode(), 
                command.getName(), 
                command.getDescription(), 
                command.getStatus(), 
                command.getUrl(), 
                command.getIp(),
                command.getUserName(),
                command.getPassword(),
                command.getToken(),
                b2BPartnerTypeDto
        ));
    }
}

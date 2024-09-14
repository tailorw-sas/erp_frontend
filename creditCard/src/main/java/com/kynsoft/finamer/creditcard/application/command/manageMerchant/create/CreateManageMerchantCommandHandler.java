package com.kynsoft.finamer.creditcard.application.command.manageMerchant.create;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.creditcard.domain.dto.ManageMerchantDto;
import com.kynsoft.finamer.creditcard.domain.dtoEnum.Status;
import com.kynsoft.finamer.creditcard.domain.services.IManageMerchantService;
import com.kynsoft.finamer.creditcard.domain.services.IManagerB2BPartnerService;
import com.kynsoft.finamer.creditcard.infrastructure.identity.ManagerB2BPartnerDto;
import org.springframework.stereotype.Component;

@Component
public class CreateManageMerchantCommandHandler implements ICommandHandler<CreateManageMerchantCommand> {

    private final IManageMerchantService service;
    private final IManagerB2BPartnerService serviceB2BPartner;

    public CreateManageMerchantCommandHandler(IManageMerchantService service, IManagerB2BPartnerService serviceB2BPartner) {
        this.service = service;
        this.serviceB2BPartner = serviceB2BPartner;
    }


    @Override
    public void handle(CreateManageMerchantCommand command) {
        ManagerB2BPartnerDto managerB2BPartnerDto = this.serviceB2BPartner.findById(command.getB2bPartner());
        service.create(new ManageMerchantDto(
                command.getId(),
                command.getCode(),
                command.getDescription(),
                managerB2BPartnerDto,
                command.getDefaultm(),
                Status.valueOf(command.getStatus())
        ));
    }
}


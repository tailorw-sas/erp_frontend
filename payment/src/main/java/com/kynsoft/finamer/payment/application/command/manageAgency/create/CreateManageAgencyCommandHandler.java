package com.kynsoft.finamer.payment.application.command.manageAgency.create;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.payment.domain.dto.ManageAgencyDto;
import com.kynsoft.finamer.payment.domain.dto.ManageAgencyTypeDto;
import com.kynsoft.finamer.payment.domain.dto.ManageClientDto;
import com.kynsoft.finamer.payment.domain.services.IManageAgencyService;
import com.kynsoft.finamer.payment.domain.services.IManageAgencyTypeService;
import com.kynsoft.finamer.payment.domain.services.IManageClientService;
import org.springframework.stereotype.Component;

@Component
public class CreateManageAgencyCommandHandler implements ICommandHandler<CreateManageAgencyCommand> {

    private final IManageAgencyService service;
    private final IManageAgencyTypeService serviceAgencyTypeService;

    private final IManageClientService clientService;

    public CreateManageAgencyCommandHandler(IManageAgencyService service,
                                            IManageAgencyTypeService serviceAgencyTypeService,
                                            IManageClientService clientService) {
        this.service = service;
        this.serviceAgencyTypeService = serviceAgencyTypeService;
        this.clientService = clientService;
    }

    @Override
    public void handle(CreateManageAgencyCommand command) {

        ManageAgencyTypeDto agencyTypeDto = this.serviceAgencyTypeService.findById(command.getAgencyType());
        ManageClientDto client = this.clientService.findById(command.getClient());
        service.create(new ManageAgencyDto(
                command.getId(),
                command.getCode(),
                command.getName(),
                command.getStatus(),
                agencyTypeDto,
                client
        ));
    }
}

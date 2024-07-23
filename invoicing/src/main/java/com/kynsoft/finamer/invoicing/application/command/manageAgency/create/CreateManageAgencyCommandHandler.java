package com.kynsoft.finamer.invoicing.application.command.manageAgency.create;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.invoicing.domain.dto.ManageAgencyDto;
import com.kynsoft.finamer.invoicing.domain.dto.ManageClientDto;
import com.kynsoft.finamer.invoicing.domain.services.IManageAgencyService;
import com.kynsoft.finamer.invoicing.domain.services.IManagerClientService;

import org.springframework.stereotype.Component;

@Component
public class CreateManageAgencyCommandHandler implements ICommandHandler<CreateManageAgencyCommand> {

    private final IManageAgencyService service;
    private final IManagerClientService managerClientService;

    public CreateManageAgencyCommandHandler(IManageAgencyService service, IManagerClientService managerClientService) {
        this.service = service;
        this.managerClientService = managerClientService;

    }

    @Override
    public void handle(CreateManageAgencyCommand command) {

        ManageClientDto clientDto = null;
        try {
            clientDto = command.getClient() != null ? this.managerClientService.findById(command.getClient()) : null;
        } catch (Exception e) {
        }

        service.create(new ManageAgencyDto(
                command.getId(),
                command.getCode(),
                command.getName(),
                clientDto,
                command.getGenerationType()
                ));
    }
}

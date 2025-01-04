package com.kynsoft.finamer.payment.application.command.manageAgency.update;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.utils.ConsumerUpdate;
import com.kynsof.share.utils.UpdateIfNotNull;
import com.kynsoft.finamer.payment.domain.dto.ManageAgencyDto;
import com.kynsoft.finamer.payment.domain.dto.ManageAgencyTypeDto;
import com.kynsoft.finamer.payment.domain.dto.ManageClientDto;
import com.kynsoft.finamer.payment.domain.services.IManageAgencyService;
import com.kynsoft.finamer.payment.domain.services.IManageAgencyTypeService;
import com.kynsoft.finamer.payment.domain.services.IManageClientService;
import org.springframework.stereotype.Component;

@Component
public class UpdateManageAgencyCommandHandler implements ICommandHandler<UpdateManageAgencyCommand> {

    private final IManageAgencyService service;
    private final IManageAgencyTypeService serviceAgencyTypeService;
    private final IManageClientService serviceClientService;

    public UpdateManageAgencyCommandHandler(IManageAgencyService service,
                                            IManageAgencyTypeService serviceAgencyTypeService,
                                            IManageClientService serviceClientService) {
        this.service = service;
        this.serviceAgencyTypeService = serviceAgencyTypeService;
        this.serviceClientService = serviceClientService;
    }

    @Override
    public void handle(UpdateManageAgencyCommand command) {

        ManageAgencyDto dto = service.findById(command.getId());
        ManageAgencyTypeDto agencyTypeDto = this.serviceAgencyTypeService.findById(command.getAgencyType());
        ManageClientDto clientDto = this.serviceClientService.findById(command.getClient());

        ConsumerUpdate update = new ConsumerUpdate();

        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(dto::setName, command.getName(), dto.getName(), update::setUpdate);
        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(dto::setStatus, command.getStatus(), dto.getStatus(), update::setUpdate);

        dto.setAgencyType(agencyTypeDto);
        dto.setClient(clientDto);
        this.service.update(dto);
    }

}

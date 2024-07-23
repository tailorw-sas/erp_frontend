package com.kynsoft.finamer.payment.application.command.manageAgency.update;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.utils.ConsumerUpdate;
import com.kynsof.share.utils.UpdateIfNotNull;
import com.kynsoft.finamer.payment.domain.dto.ManageAgencyDto;
import com.kynsoft.finamer.payment.domain.dto.ManageAgencyTypeDto;
import com.kynsoft.finamer.payment.domain.services.IManageAgencyService;
import com.kynsoft.finamer.payment.domain.services.IManageAgencyTypeService;
import org.springframework.stereotype.Component;

@Component
public class UpdateManageAgencyCommandHandler implements ICommandHandler<UpdateManageAgencyCommand> {

    private final IManageAgencyService service;
    private final IManageAgencyTypeService serviceAgencyTypeService;

    public UpdateManageAgencyCommandHandler(IManageAgencyService service,
                                            IManageAgencyTypeService serviceAgencyTypeService) {
        this.service = service;
        this.serviceAgencyTypeService = serviceAgencyTypeService;
    }

    @Override
    public void handle(UpdateManageAgencyCommand command) {

        ManageAgencyDto dto = service.findById(command.getId());
        ManageAgencyTypeDto agencyTypeDto = this.serviceAgencyTypeService.findById(command.getAgencyType());

        ConsumerUpdate update = new ConsumerUpdate();

        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(dto::setName, command.getName(), dto.getName(), update::setUpdate);
        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(dto::setStatus, command.getStatus(), dto.getStatus(), update::setUpdate);

        dto.setAgencyType(agencyTypeDto);
        this.service.update(dto);
    }

}

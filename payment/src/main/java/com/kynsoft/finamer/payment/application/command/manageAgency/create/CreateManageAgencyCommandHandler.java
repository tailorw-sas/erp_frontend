package com.kynsoft.finamer.payment.application.command.manageAgency.create;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.payment.domain.dto.ManageAgencyDto;
import com.kynsoft.finamer.payment.domain.dto.ManageAgencyTypeDto;
import com.kynsoft.finamer.payment.domain.services.IManageAgencyService;
import com.kynsoft.finamer.payment.domain.services.IManageAgencyTypeService;
import org.springframework.stereotype.Component;

@Component
public class CreateManageAgencyCommandHandler implements ICommandHandler<CreateManageAgencyCommand> {

    private final IManageAgencyService service;
    private final IManageAgencyTypeService serviceAgencyTypeService;

    public CreateManageAgencyCommandHandler(IManageAgencyService service,
                                            IManageAgencyTypeService serviceAgencyTypeService) {
        this.service = service;
        this.serviceAgencyTypeService = serviceAgencyTypeService;
    }

    @Override
    public void handle(CreateManageAgencyCommand command) {

        ManageAgencyTypeDto agencyTypeDto = this.serviceAgencyTypeService.findById(command.getAgencyType());
        service.create(new ManageAgencyDto(
                command.getId(),
                command.getCode(),
                command.getName(),
                command.getStatus(),
                agencyTypeDto
        ));
    }
}

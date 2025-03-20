package com.kynsoft.finamer.payment.application.command.manageAgency.create;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.payment.domain.dto.ManageAgencyDto;
import com.kynsoft.finamer.payment.domain.dto.ManageAgencyTypeDto;
import com.kynsoft.finamer.payment.domain.dto.ManageClientDto;
import com.kynsoft.finamer.payment.domain.dto.ManageCountryDto;
import com.kynsoft.finamer.payment.domain.services.IManageAgencyService;
import com.kynsoft.finamer.payment.domain.services.IManageAgencyTypeService;
import com.kynsoft.finamer.payment.domain.services.IManageClientService;
import com.kynsoft.finamer.payment.domain.services.IManageCountryService;
import org.springframework.stereotype.Component;

@Component
public class CreateManageAgencyCommandHandler implements ICommandHandler<CreateManageAgencyCommand> {

    private final IManageAgencyService service;
    private final IManageAgencyTypeService manageAgencyTypeService;
    private final IManageClientService manageClientService;
    private final IManageCountryService manageCountryService;

    public CreateManageAgencyCommandHandler(IManageAgencyService service,
                                            IManageAgencyTypeService manageAgencyTypeService,
                                            IManageClientService manageClientService,
                                            IManageCountryService manageCountryService) {
        this.service = service;
        this.manageAgencyTypeService = manageAgencyTypeService;
        this.manageClientService = manageClientService;
        this.manageCountryService = manageCountryService;
    }

    @Override
    public void handle(CreateManageAgencyCommand command) {

        ManageAgencyTypeDto agencyTypeDto = this.manageAgencyTypeService.findById(command.getAgencyType());
        ManageClientDto client = this.manageClientService.findById(command.getClient());
        ManageCountryDto country = this.manageCountryService.findById(command.getCountry());

        service.create(new ManageAgencyDto(
                command.getId(),
                command.getCode(),
                command.getName(),
                command.getStatus(),
                agencyTypeDto,
                client,
                country
        ));
    }
}

package com.kynsoft.finamer.payment.application.command.manageAgency.update;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.utils.ConsumerUpdate;
import com.kynsof.share.utils.UpdateIfNotNull;
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
public class UpdateManageAgencyCommandHandler implements ICommandHandler<UpdateManageAgencyCommand> {

    private final IManageAgencyService service;
    private final IManageAgencyTypeService manageAgencyTypeService;
    private final IManageClientService manageClientService;
    private final IManageCountryService manageCountryService;

    public UpdateManageAgencyCommandHandler(IManageAgencyService service,
                                            IManageAgencyTypeService manageAgencyTypeService,
                                            IManageClientService manageClientService,
                                            IManageCountryService manageCountryService) {
        this.service = service;
        this.manageAgencyTypeService = manageAgencyTypeService;
        this.manageClientService = manageClientService;
        this.manageCountryService = manageCountryService;
    }

    @Override
    public void handle(UpdateManageAgencyCommand command) {

        ManageAgencyDto dto = service.findById(command.getId());
        ManageAgencyTypeDto agencyTypeDto = this.manageAgencyTypeService.findById(command.getAgencyType());
        ManageClientDto clientDto = this.manageClientService.findById(command.getClient());
        ManageCountryDto countryDto = this.manageCountryService.findById(command.getCountry());

        ConsumerUpdate update = new ConsumerUpdate();

        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(dto::setName, command.getName(), dto.getName(), update::setUpdate);
        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(dto::setStatus, command.getStatus(), dto.getStatus(), update::setUpdate);

        dto.setAgencyType(agencyTypeDto);
        dto.setClient(clientDto);
        dto.setCountry(countryDto);

        this.service.update(dto);
    }

}

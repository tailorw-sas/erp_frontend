package com.kynsoft.finamer.invoicing.application.command.manageAgency.update;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.utils.ConsumerUpdate;
import com.kynsof.share.utils.UpdateIfNotNull;
import com.kynsoft.finamer.invoicing.domain.dto.ManageAgencyDto;
import com.kynsoft.finamer.invoicing.domain.services.IManageAgencyService;
import com.kynsoft.finamer.invoicing.domain.services.IManageCityStateService;
import com.kynsoft.finamer.invoicing.domain.services.IManagerB2BPartnerService;
import com.kynsoft.finamer.invoicing.domain.services.IManagerClientService;

import com.kynsoft.finamer.invoicing.domain.services.IManagerCountryService;
import org.springframework.stereotype.Component;

@Component
public class UpdateManageAgencyCommandHandler implements ICommandHandler<UpdateManageAgencyCommand> {

    private final IManageAgencyService service;
    private final IManagerClientService clientService;
    private final IManagerCountryService managerCountryService;
    private  final IManageCityStateService manageCityStateService;
    private final IManagerB2BPartnerService managerB2BPartnerService;

    public UpdateManageAgencyCommandHandler(IManageAgencyService service, IManagerClientService clientService,
                                            IManagerCountryService managerCountryService,
                                            IManageCityStateService manageCityStateService,
                                            IManagerB2BPartnerService managerB2BPartnerService) {
        this.service = service;
        this.clientService = clientService;

        this.managerCountryService = managerCountryService;
        this.manageCityStateService = manageCityStateService;
        this.managerB2BPartnerService = managerB2BPartnerService;
    }

    @Override
    public void handle(UpdateManageAgencyCommand command) {

        ManageAgencyDto dto = service.findById(command.getId());

        ConsumerUpdate update = new ConsumerUpdate();

        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(dto::setName, command.getName(), dto.getName(),
                update::setUpdate);
        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(dto::setCif, command.getCif(), dto.getCif(),
                update::setUpdate);
        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(dto::setAddress, command.getAddress(), dto.getAddress(),
                update::setUpdate);

        UpdateIfNotNull.updateEntity(dto::setClient, command.getClient(), dto.getClient().getId(), update::setUpdate,
                clientService::findById);
        UpdateIfNotNull.updateEntity(dto::setSentB2BPartner, command.getSentB2BPartner(), dto.getSentB2BPartner().getId(), update::setUpdate,
                managerB2BPartnerService::findById);
        if (update.getUpdate() > 0) {
            this.service.update(dto);
        }


    }

}

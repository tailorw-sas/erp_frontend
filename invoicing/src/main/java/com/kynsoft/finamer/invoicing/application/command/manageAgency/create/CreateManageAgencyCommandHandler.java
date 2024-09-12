package com.kynsoft.finamer.invoicing.application.command.manageAgency.create;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.invoicing.domain.dto.ManageAgencyDto;
import com.kynsoft.finamer.invoicing.domain.dto.ManageCityStateDto;
import com.kynsoft.finamer.invoicing.domain.dto.ManageClientDto;
import com.kynsoft.finamer.invoicing.domain.dto.ManagerB2BPartnerDto;
import com.kynsoft.finamer.invoicing.domain.dto.ManagerCountryDto;
import com.kynsoft.finamer.invoicing.domain.services.IManageAgencyService;
import com.kynsoft.finamer.invoicing.domain.services.IManageCityStateService;
import com.kynsoft.finamer.invoicing.domain.services.IManagerB2BPartnerService;
import com.kynsoft.finamer.invoicing.domain.services.IManagerClientService;

import com.kynsoft.finamer.invoicing.domain.services.IManagerCountryService;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class CreateManageAgencyCommandHandler implements ICommandHandler<CreateManageAgencyCommand> {

    private final IManageAgencyService service;
    private final IManagerClientService managerClientService;
    private final IManagerCountryService managerCountryService;
    private  final IManageCityStateService manageCityStateService;

    private final IManagerB2BPartnerService managerB2BPartnerService;
    public CreateManageAgencyCommandHandler(IManageAgencyService service,
                                            IManagerClientService managerClientService,
                                            IManagerCountryService managerCountryService,
                                            IManageCityStateService manageCityStateService,
                                            IManagerB2BPartnerService managerB2BPartnerService) {
        this.service = service;
        this.managerClientService = managerClientService;
        this.managerCountryService = managerCountryService;
        this.manageCityStateService = manageCityStateService;
        this.managerB2BPartnerService = managerB2BPartnerService;
    }

    @Override
    public void handle(CreateManageAgencyCommand command) {

        ManageClientDto clientDto = null;
        ManagerB2BPartnerDto b2BPartnerDto =null;
        ManageCityStateDto cityStateDto=null;
        ManagerCountryDto managerCountryDto=null;

        try {
            clientDto = command.getClient() != null ? this.managerClientService.findById(command.getClient()) : null;
            managerCountryDto= Objects.nonNull(command.getCountry())?managerCountryService.findById(command.getCountry()):null;
            cityStateDto= Objects.nonNull(command.getCityState())?manageCityStateService.findById(command.getCityState()):null;
            b2BPartnerDto =Objects.nonNull(command.getSentB2BPartner())?managerB2BPartnerService.findById(command.getSentB2BPartner()):null;
        } catch (Exception e) {
        }

        service.create(new ManageAgencyDto(
                command.getId(),
                command.getCode(),
                command.getName(),
                clientDto,
                command.getGenerationType(),
                command.getStatus(),
                command.getCif(),
                command.getAddress(),
                b2BPartnerDto,
                cityStateDto,
                managerCountryDto,
                command.getMailingAddress()
        ));
    }
}

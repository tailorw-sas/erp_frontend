package com.kynsoft.finamer.invoicing.application.command.manageCityState.create;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.invoicing.domain.dto.ManageCityStateDto;
import com.kynsoft.finamer.invoicing.domain.dto.ManagerCountryDto;
import com.kynsoft.finamer.invoicing.domain.dto.ManagerTimeZoneDto;
import com.kynsoft.finamer.invoicing.domain.services.IManageCityStateService;
import com.kynsoft.finamer.invoicing.domain.services.IManagerCountryService;
import com.kynsoft.finamer.invoicing.domain.services.IManagerTimeZoneService;
import org.springframework.stereotype.Component;

@Component
public class CreateManageCityStateCommandHandler implements ICommandHandler<CreateManageCityStateCommand> {

    private final IManageCityStateService service;
    private final IManagerCountryService serviceCountry;
    private final IManagerTimeZoneService timeZoneService;

    public CreateManageCityStateCommandHandler(IManageCityStateService service,
                                               IManagerCountryService serviceCountry,
                                               IManagerTimeZoneService timeZoneService) {
        this.service = service;
        this.serviceCountry = serviceCountry;
        this.timeZoneService = timeZoneService;
    }

    @Override
    public void handle(CreateManageCityStateCommand command) {
        ManagerCountryDto country = this.serviceCountry.findById(command.getCountry());
        ManagerTimeZoneDto timeZoneDto = this.timeZoneService.findById(command.getTimeZone());

        service.create(new ManageCityStateDto(
                command.getId(),
                command.getCode(),
                command.getName(),
                command.getDescription(),
                command.getStatus(),
                country,
                timeZoneDto
        ));
    }
}

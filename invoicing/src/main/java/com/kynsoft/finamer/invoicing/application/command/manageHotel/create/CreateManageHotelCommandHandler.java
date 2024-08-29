package com.kynsoft.finamer.invoicing.application.command.manageHotel.create;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.invoicing.domain.dto.ManageHotelDto;
import com.kynsoft.finamer.invoicing.domain.dto.ManageTradingCompaniesDto;
import com.kynsoft.finamer.invoicing.domain.services.IManageHotelService;
import com.kynsoft.finamer.invoicing.domain.services.IManageTradingCompaniesService;

import org.springframework.stereotype.Component;

@Component
public class CreateManageHotelCommandHandler implements ICommandHandler<CreateManageHotelCommand> {

    private final IManageHotelService service;
    private final IManageTradingCompaniesService manageTradingCompaniesService;

    public CreateManageHotelCommandHandler(IManageHotelService service,
            IManageTradingCompaniesService manageTradingCompaniesService) {
        this.service = service;
        this.manageTradingCompaniesService = manageTradingCompaniesService;

    }

    @Override
    public void handle(CreateManageHotelCommand command) {

        ManageTradingCompaniesDto manageTradingCompaniesDto = command.getManageTradingCompany() != null
                ? this.manageTradingCompaniesService
                        .findById(command.getManageTradingCompany())
                : null;

        service.create(new ManageHotelDto(
                command.getId(), command.getCode(), command.getName(), manageTradingCompaniesDto, null,command.isVirtual(), command.getStatus(),command.isRequiresFlatRate(), command.getAutoApplyCredit()));
    }
}

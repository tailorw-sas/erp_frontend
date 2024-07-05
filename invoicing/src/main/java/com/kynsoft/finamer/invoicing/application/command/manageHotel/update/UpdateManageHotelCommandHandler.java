package com.kynsoft.finamer.invoicing.application.command.manageHotel.update;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.utils.ConsumerUpdate;
import com.kynsof.share.utils.UpdateIfNotNull;
import com.kynsoft.finamer.invoicing.domain.dto.ManageHotelDto;
import com.kynsoft.finamer.invoicing.domain.services.IManageHotelService;
import com.kynsoft.finamer.invoicing.domain.services.IManageTradingCompaniesService;

import org.springframework.stereotype.Component;

@Component
public class UpdateManageHotelCommandHandler implements ICommandHandler<UpdateManageHotelCommand> {

    private final IManageHotelService service;

    private final IManageTradingCompaniesService tradingCompaniesService;

    public UpdateManageHotelCommandHandler(IManageHotelService service,
            IManageTradingCompaniesService tradingCompaniesService) {
        this.service = service;
        this.tradingCompaniesService = tradingCompaniesService;

    }

    @Override
    public void handle(UpdateManageHotelCommand command) {

        ManageHotelDto dto = service.findById(command.getId());

        ConsumerUpdate update = new ConsumerUpdate();

        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(dto::setName, command.getName(), dto.getName(),
                update::setUpdate);

        UpdateIfNotNull.updateEntity(dto::setManageTradingCompanies, command.getTradingCompany(),
                dto.getManageTradingCompanies().getId(), update::setUpdate, tradingCompaniesService::findById);
    }
}

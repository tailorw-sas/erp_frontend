package com.kynsoft.finamer.invoicing.application.command.manageHotel.update;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.utils.ConsumerUpdate;
import com.kynsof.share.utils.UpdateIfNotNull;
import com.kynsoft.finamer.invoicing.domain.dto.ManageHotelDto;
import com.kynsoft.finamer.invoicing.domain.dto.ManageTradingCompaniesDto;
import com.kynsoft.finamer.invoicing.domain.services.IManageHotelService;
import com.kynsoft.finamer.invoicing.domain.services.IManageTradingCompaniesService;

import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.function.Consumer;

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

        if(dto.getManageTradingCompanies() != null) {
            updateTradingCompanies(dto::setManageTradingCompanies, command.getTradingCompany(), dto.getManageTradingCompanies().getId(), update::setUpdate);
        } else if(command.getTradingCompany() != null){
            dto.setManageTradingCompanies(tradingCompaniesService.findById(command.getTradingCompany()));
            update.setUpdate(1);
        }

        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(dto::setStatus, command.getStatus(), dto.getStatus(), update::setUpdate);
        UpdateIfNotNull.updateBoolean(dto::setVirtual, command.getIsVirtual(), dto.isVirtual(), update::setUpdate);
        UpdateIfNotNull.updateBoolean(dto::setRequiresFlatRate, command.isRequiresFlatRate(), dto.isRequiresFlatRate(), update::setUpdate);
        UpdateIfNotNull.updateBoolean(dto::setAutoApplyCredit, command.getAutoApplyCredit(), dto.getAutoApplyCredit(), update::setUpdate);

        if (update.getUpdate() > 0) {
            this.service.update(dto);
        }
    }

    private boolean updateTradingCompanies(Consumer<ManageTradingCompaniesDto> setter, UUID newValue, UUID oldValue, Consumer<Integer> update) {
        if (newValue != null) {
            if(!newValue.equals(oldValue)) {
                ManageTradingCompaniesDto tradingCompaniesDto = tradingCompaniesService.findById(newValue);
                setter.accept(tradingCompaniesDto);
                update.accept(1);

                return true;
            }
        } else {
            setter.accept(null);
            update.accept(1);
            return true;
        }
        return false;
    }
}

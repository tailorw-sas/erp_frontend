package com.kynsoft.finamer.payment.application.command.manageHotel.update;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.payment.domain.dto.ManageHotelDto;
import com.kynsoft.finamer.payment.domain.services.IManageHotelService;
import org.springframework.stereotype.Component;

@Component
public class UpdateManageHotelCommandHandler implements ICommandHandler<UpdateManageHotelCommand> {

    private final IManageHotelService service;

    public UpdateManageHotelCommandHandler(IManageHotelService service) {
        this.service = service;

    }

    @Override
    public void handle(UpdateManageHotelCommand command) {

        ManageHotelDto dto = this.service.findById(command.getId());
        dto.setName(command.getName());
        dto.setStatus(command.getStatus());
        dto.setApplyByTradingCompany(command.getApplyByTradingCompany());
        dto.setManageTradingCompany(command.getManageTradingCompany());
        dto.setAutoApplyCredit(command.getAutoApplyCredit());
        service.update(dto);
    }
}

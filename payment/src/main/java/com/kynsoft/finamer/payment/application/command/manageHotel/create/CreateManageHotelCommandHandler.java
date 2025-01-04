package com.kynsoft.finamer.payment.application.command.manageHotel.create;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.payment.domain.dto.ManageHotelDto;
import com.kynsoft.finamer.payment.domain.services.IManageHotelService;
import org.springframework.stereotype.Component;

@Component
public class CreateManageHotelCommandHandler implements ICommandHandler<CreateManageHotelCommand> {

    private final IManageHotelService service;

    public CreateManageHotelCommandHandler(IManageHotelService service) {
        this.service = service;

    }

    @Override
    public void handle(CreateManageHotelCommand command) {

        service.create(new ManageHotelDto(
                command.getId(), 
                command.getCode(), 
                command.getName(), 
                command.getStatus(), 
                command.getApplyByTradingCompany(),
                command.getManageTradingCompany(),
                command.getAutoApplyCredit()
        ));
    }
}

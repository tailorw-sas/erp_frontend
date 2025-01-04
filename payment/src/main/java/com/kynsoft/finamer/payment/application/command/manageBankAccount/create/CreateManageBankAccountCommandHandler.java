package com.kynsoft.finamer.payment.application.command.manageBankAccount.create;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.payment.domain.dto.ManageBankAccountDto;
import com.kynsoft.finamer.payment.domain.dto.ManageHotelDto;
import com.kynsoft.finamer.payment.domain.services.IManageBankAccountService;
import com.kynsoft.finamer.payment.domain.services.IManageHotelService;
import org.springframework.stereotype.Component;

@Component
public class CreateManageBankAccountCommandHandler implements ICommandHandler<CreateManageBankAccountCommand> {

    private final IManageBankAccountService service;
    private final IManageHotelService hotelService;

    public CreateManageBankAccountCommandHandler(IManageBankAccountService service, IManageHotelService hotelService) {
        this.service = service;
        this.hotelService = hotelService;
    }

    @Override
    public void handle(CreateManageBankAccountCommand command) {
         ManageHotelDto manageHotelDto =hotelService.findById(command.getManageHotel());
         service.create(new ManageBankAccountDto(command.getId(), command.getAccountNumber(), command.getStatus(), command.getNameOfBank(),manageHotelDto));

    }
}

package com.kynsoft.finamer.invoicing.application.command.manageBankAccount.create;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.invoicing.domain.dto.ManageBankAccountDto;
import com.kynsoft.finamer.invoicing.domain.dto.ManageHotelDto;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.Status;
import com.kynsoft.finamer.invoicing.domain.services.IManageBankAccountService;
import com.kynsoft.finamer.invoicing.domain.services.IManageHotelService;
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
         service.create(new ManageBankAccountDto(command.getId(),
                 Status.valueOf(command.getStatus()),
                 command.getAccountNumber(),
                 command.getNameOfBank(),manageHotelDto));

    }
}

package com.kynsoft.finamer.invoicing.application.command.manageBankAccount.update;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.invoicing.domain.dto.ManageBankAccountDto;
import com.kynsoft.finamer.invoicing.domain.dto.ManageHotelDto;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.Status;
import com.kynsoft.finamer.invoicing.domain.services.IManageBankAccountService;
import com.kynsoft.finamer.invoicing.domain.services.IManageHotelService;
import org.springframework.stereotype.Component;

@Component
public class UpdateManageBankAccountCommandHandler implements ICommandHandler<UpdateManageBankAccountCommand> {

    private final IManageBankAccountService service;
    private final IManageHotelService hotelService;

    public UpdateManageBankAccountCommandHandler(IManageBankAccountService service,
                                                 IManageHotelService hotelService) {
        this.service = service;
        this.hotelService = hotelService;
    }

    @Override
    public void handle(UpdateManageBankAccountCommand command) {

        ManageBankAccountDto dto = this.service.findById(command.getId());
        dto.setNameOfBank(command.getNameOfBank());
        dto.setStatus(Status.valueOf(command.getStatus()));
        dto.setAccountNumber(command.getAccountNumber());
        ManageHotelDto manageHotelDto =hotelService.findById(command.getId());
        dto.setManageHotelDto(manageHotelDto);
        service.update(dto);
    }
}

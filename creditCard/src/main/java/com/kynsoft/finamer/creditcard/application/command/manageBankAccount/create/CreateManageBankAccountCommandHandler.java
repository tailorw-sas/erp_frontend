package com.kynsoft.finamer.creditcard.application.command.manageBankAccount.create;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.creditcard.domain.dto.ManageBankAccountDto;
import com.kynsoft.finamer.creditcard.domain.dto.ManageHotelDto;
import com.kynsoft.finamer.creditcard.domain.dto.ManagerAccountTypeDto;
import com.kynsoft.finamer.creditcard.domain.dto.ManagerBankDto;
import com.kynsoft.finamer.creditcard.domain.services.IManageBankAccountService;
import com.kynsoft.finamer.creditcard.domain.services.IManageHotelService;
import com.kynsoft.finamer.creditcard.domain.services.IManagerAccountTypeService;
import com.kynsoft.finamer.creditcard.domain.services.IManagerBankService;
import org.springframework.stereotype.Component;

@Component
public class CreateManageBankAccountCommandHandler implements ICommandHandler<CreateManageBankAccountCommand> {

    private final IManageBankAccountService service;

    private final IManagerBankService bankService;

    private final IManageHotelService hotelService;

    private final IManagerAccountTypeService accountTypeService;

    public CreateManageBankAccountCommandHandler(IManageBankAccountService service, 
                                                 IManagerBankService bankService, 
                                                 IManageHotelService hotelService, 
                                                 IManagerAccountTypeService accountTypeService) {
        this.service = service;
        this.bankService = bankService;
        this.hotelService = hotelService;
        this.accountTypeService = accountTypeService;
    }

    @Override
    public void handle(CreateManageBankAccountCommand command) {
        ManagerBankDto manageBankDto = bankService.findById(command.getManageBank());
        ManageHotelDto manageHotelDto = hotelService.findById(command.getManageHotel());
        ManagerAccountTypeDto manageAccountTypeDto = accountTypeService.findById(command.getManageAccountType());

        service.create(new ManageBankAccountDto(
                command.getId(), command.getStatus(), command.getAccountNumber(),
                manageBankDto, manageHotelDto, manageAccountTypeDto, command.getDescription()
        ));
    }
}

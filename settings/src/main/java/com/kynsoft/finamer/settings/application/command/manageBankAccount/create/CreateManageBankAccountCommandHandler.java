package com.kynsoft.finamer.settings.application.command.manageBankAccount.create;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.kafka.entity.ReplicateManageBankAccountKafka;
import com.kynsoft.finamer.settings.domain.dto.ManageBankAccountDto;
import com.kynsoft.finamer.settings.domain.dto.ManageHotelDto;
import com.kynsoft.finamer.settings.domain.dto.ManagerAccountTypeDto;
import com.kynsoft.finamer.settings.domain.dto.ManagerBankDto;
import com.kynsoft.finamer.settings.domain.rules.manageBankAccount.ManageBankAccountNumberMustBeUniqueRule;
import com.kynsoft.finamer.settings.domain.rules.manageBankAccount.ManageBankAccountNumberRule;
import com.kynsoft.finamer.settings.domain.services.IManageBankAccountService;
import com.kynsoft.finamer.settings.domain.services.IManageHotelService;
import com.kynsoft.finamer.settings.domain.services.IManagerAccountTypeService;
import com.kynsoft.finamer.settings.domain.services.IManagerBankService;
import com.kynsoft.finamer.settings.infrastructure.services.kafka.producer.manageBankAccount.ProducerReplicateManageBankAccount;
import org.springframework.stereotype.Component;

@Component
public class CreateManageBankAccountCommandHandler implements ICommandHandler<CreateManageBankAccountCommand> {

    private final IManageBankAccountService service;

    private final IManagerBankService bankService;

    private final IManageHotelService hotelService;

    private final IManagerAccountTypeService accountTypeService;

    private final ProducerReplicateManageBankAccount producerReplicateManageBankAccount;

    public CreateManageBankAccountCommandHandler(IManageBankAccountService service, 
                                                 IManagerBankService bankService, 
                                                 IManageHotelService hotelService, 
                                                 IManagerAccountTypeService accountTypeService,
                                                 ProducerReplicateManageBankAccount producerReplicateManageBankAccount) {
        this.service = service;
        this.bankService = bankService;
        this.hotelService = hotelService;
        this.accountTypeService = accountTypeService;
        this.producerReplicateManageBankAccount = producerReplicateManageBankAccount;
    }

    @Override
    public void handle(CreateManageBankAccountCommand command) {
        RulesChecker.checkRule(new ManageBankAccountNumberMustBeUniqueRule(this.service, command.getAccountNumber(), command.getId()));
        RulesChecker.checkRule(new ManageBankAccountNumberRule(command.getAccountNumber()));

        ManagerBankDto manageBankDto = bankService.findById(command.getManageBank());
        ManageHotelDto manageHotelDto = hotelService.findById(command.getManageHotel());
        ManagerAccountTypeDto manageAccountTypeDto = accountTypeService.findById(command.getManageAccountType());

        service.create(new ManageBankAccountDto(
                command.getId(), command.getStatus(), command.getAccountNumber(),
                manageBankDto, manageHotelDto, manageAccountTypeDto, command.getDescription()
        ));
        this.producerReplicateManageBankAccount.create(new ReplicateManageBankAccountKafka(command.getId(), command.getAccountNumber(), command.getStatus().name(), manageBankDto.getName(),manageHotelDto.getId()));
    }
}

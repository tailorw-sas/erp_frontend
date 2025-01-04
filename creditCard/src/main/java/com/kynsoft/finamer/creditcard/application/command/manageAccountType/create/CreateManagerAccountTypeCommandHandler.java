package com.kynsoft.finamer.creditcard.application.command.manageAccountType.create;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.creditcard.domain.dto.ManagerAccountTypeDto;
import com.kynsoft.finamer.creditcard.domain.services.IManagerAccountTypeService;
import org.springframework.stereotype.Component;

@Component
public class CreateManagerAccountTypeCommandHandler implements ICommandHandler<CreateManagerAccountTypeCommand> {

    private final IManagerAccountTypeService service;

    public CreateManagerAccountTypeCommandHandler(IManagerAccountTypeService service) {
        this.service = service;
    }

    @Override
    public void handle(CreateManagerAccountTypeCommand command) {
//        RulesChecker.checkRule(new ManagerAccountTypeCodeSizeRule(command.getCode()));
//        RulesChecker.checkRule(new ManagerAccountTypeNameMustBeNullRule(command.getName()));
//        RulesChecker.checkRule(new ManagerAccountTypeCodeMustBeUniqueRule(this.service, command.getCode(), command.getId()));

        service.create(new ManagerAccountTypeDto(
                command.getId(),
                command.getCode(),
                command.getName(),
                command.getDescription(),
                command.getStatus(),
                command.isModuleVcc(),
                command.isModulePayment()
        ));
    }
}

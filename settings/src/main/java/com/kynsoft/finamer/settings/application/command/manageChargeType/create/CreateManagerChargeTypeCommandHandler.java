package com.kynsoft.finamer.settings.application.command.manageChargeType.create;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.settings.domain.dto.ManagerChargeTypeDto;
import com.kynsoft.finamer.settings.domain.rules.managerChargeType.ManagerChargeTypeCodeMustBeUniqueRule;
import com.kynsoft.finamer.settings.domain.rules.managerChargeType.ManagerChargeTypeCodeSizeRule;
import com.kynsoft.finamer.settings.domain.rules.managerChargeType.ManagerChargeTypeNameMustBeNullRule;
import com.kynsoft.finamer.settings.domain.services.IManagerChargeTypeService;
import org.springframework.stereotype.Component;

@Component
public class CreateManagerChargeTypeCommandHandler implements ICommandHandler<CreateManagerChargeTypeCommand> {

    private final IManagerChargeTypeService service;

    public CreateManagerChargeTypeCommandHandler(IManagerChargeTypeService service) {
        this.service = service;
    }

    @Override
    public void handle(CreateManagerChargeTypeCommand command) {
        RulesChecker.checkRule(new ManagerChargeTypeCodeSizeRule(command.getCode()));
        RulesChecker.checkRule(new ManagerChargeTypeNameMustBeNullRule(command.getName()));
        RulesChecker.checkRule(new ManagerChargeTypeCodeMustBeUniqueRule(this.service, command.getCode(), command.getId()));

        service.create(new ManagerChargeTypeDto(
                command.getId(),
                command.getCode(),
                command.getName(),
                command.getDescription(),
                command.getStatus()
        ));
    }
}

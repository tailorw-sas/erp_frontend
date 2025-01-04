package com.kynsoft.finamer.invoicing.application.command.manageNightType.create;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.invoicing.domain.dto.ManageNightTypeDto;
import com.kynsoft.finamer.invoicing.domain.rules.manageNightType.ManageNightTypeCodeMustBeUniqueRule;
import com.kynsoft.finamer.invoicing.domain.rules.manageNightType.ManageNightTypeCodeSizeRule;
import com.kynsoft.finamer.invoicing.domain.rules.manageNightType.ManageNightTypeNameMustBeNullRule;
import com.kynsoft.finamer.invoicing.domain.services.IManageNightTypeService;
import org.springframework.stereotype.Component;

@Component
public class CreateManageNightTypeCommandHandler implements ICommandHandler<CreateManageNightTypeCommand> {

    private final IManageNightTypeService service;

    public CreateManageNightTypeCommandHandler(IManageNightTypeService service) {
        this.service = service;
    }

    @Override
    public void handle(CreateManageNightTypeCommand command) {
        RulesChecker.checkRule(new ManageNightTypeCodeSizeRule(command.getCode()));
        RulesChecker.checkRule(new ManageNightTypeNameMustBeNullRule(command.getName()));
        RulesChecker.checkRule(new ManageNightTypeCodeMustBeUniqueRule(this.service, command.getCode(), command.getId()));

        service.create(new ManageNightTypeDto(
                command.getId(),
                command.getCode(),
                command.getName(),
                command.getStatus()
        ));
    }
}

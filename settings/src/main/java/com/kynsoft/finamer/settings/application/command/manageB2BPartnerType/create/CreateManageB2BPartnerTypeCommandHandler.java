package com.kynsoft.finamer.settings.application.command.manageB2BPartnerType.create;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.settings.domain.dto.ManageB2BPartnerTypeDto;
import com.kynsoft.finamer.settings.domain.rules.managerB2BPartnerType.ManageB2BPartnerTypeCodeMustBeUniqueRule;
import com.kynsoft.finamer.settings.domain.rules.managerB2BPartnerType.ManageB2BPartnerTypeCodeSizeRule;
import com.kynsoft.finamer.settings.domain.rules.managerB2BPartnerType.ManageB2BPartnerTypeNameMustBeNullRule;
import com.kynsoft.finamer.settings.domain.services.IManageB2BPartnerTypeService;
import org.springframework.stereotype.Component;

@Component
public class CreateManageB2BPartnerTypeCommandHandler implements ICommandHandler<CreateManageB2BPartnerTypeCommand> {

    private final IManageB2BPartnerTypeService service;

    public CreateManageB2BPartnerTypeCommandHandler(IManageB2BPartnerTypeService service) {
        this.service = service;
    }

    @Override
    public void handle(CreateManageB2BPartnerTypeCommand command) {
        RulesChecker.checkRule(new ManageB2BPartnerTypeCodeSizeRule(command.getCode()));
        RulesChecker.checkRule(new ManageB2BPartnerTypeNameMustBeNullRule(command.getName()));
        RulesChecker.checkRule(new ManageB2BPartnerTypeCodeMustBeUniqueRule(this.service, command.getCode(), command.getId()));

        service.create(new ManageB2BPartnerTypeDto(
                command.getId(),
                command.getCode(),
                command.getName(),
                command.getDescription(),
                command.getStatus()
        ));
    }
}

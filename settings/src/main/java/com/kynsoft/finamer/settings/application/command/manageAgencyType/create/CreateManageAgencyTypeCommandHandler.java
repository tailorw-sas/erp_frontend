package com.kynsoft.finamer.settings.application.command.manageAgencyType.create;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.settings.domain.dto.ManageAgencyTypeDto;
import com.kynsoft.finamer.settings.domain.rules.manageAgencyType.ManageAgencyTypeCodeMustBeUniqueRule;
import com.kynsoft.finamer.settings.domain.rules.manageAgencyType.ManageAgencyTypeCodeSizeRule;
import com.kynsoft.finamer.settings.domain.rules.manageAgencyType.ManageAgencyTypeNameMustBeNull;
import com.kynsoft.finamer.settings.domain.services.IManageAgencyTypeService;
import org.springframework.stereotype.Component;

@Component
public class CreateManageAgencyTypeCommandHandler implements ICommandHandler<CreateManageAgencyTypeCommand> {

    private final IManageAgencyTypeService service;

    public CreateManageAgencyTypeCommandHandler(IManageAgencyTypeService service) {
        this.service = service;
    }

    @Override
    public void handle(CreateManageAgencyTypeCommand command) {
        RulesChecker.checkRule(new ManageAgencyTypeCodeSizeRule(command.getCode()));
        RulesChecker.checkRule(new ManageAgencyTypeNameMustBeNull(command.getName()));
        RulesChecker.checkRule(new ManageAgencyTypeCodeMustBeUniqueRule(this.service, command.getCode(), command.getId()));

        service.create(new ManageAgencyTypeDto(
                command.getId(),
                command.getCode(),
                command.getStatus(),
                command.getName()
        ));
    }
}

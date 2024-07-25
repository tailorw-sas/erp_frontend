package com.kynsoft.finamer.payment.application.command.resourceType.create;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.payment.domain.dto.ResourceTypeDto;
import com.kynsoft.finamer.payment.domain.rules.resourceType.ResourceDefaultMustBeUniqueRule;
import com.kynsoft.finamer.payment.domain.rules.resourceType.ResourceTypeCodeMustBeUniqueRule;
import com.kynsoft.finamer.payment.domain.services.IManageResourceTypeService;
import org.springframework.stereotype.Component;

@Component
public class CreateManageResourceTypeCommandHandler implements ICommandHandler<CreateManageResourceTypeCommand> {

    private final IManageResourceTypeService service;

    public CreateManageResourceTypeCommandHandler(IManageResourceTypeService service) {
        this.service = service;
    }

    @Override
    public void handle(CreateManageResourceTypeCommand command) {

        RulesChecker.checkRule(new ResourceTypeCodeMustBeUniqueRule(this.service, command.getCode(), command.getId()));
        if (command.getDefaults()) {
            RulesChecker.checkRule(new ResourceDefaultMustBeUniqueRule(this.service, command.getId()));
        }

        service.create(new ResourceTypeDto(
                command.getId(),
                command.getCode(),
                command.getName(),
                command.getDescription(),
                command.getStatus(),
                command.getDefaults()
        ));
    }
}

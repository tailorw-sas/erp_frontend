package com.kynsoft.finamer.creditcard.application.command.manageAttachmentType.create;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.creditcard.domain.dto.ManageAttachmentTypeDto;
import com.kynsoft.finamer.creditcard.domain.rules.manageAttachmentType.ManageAttachmentTypeCodeMustBeUniqueRule;
import com.kynsoft.finamer.creditcard.domain.rules.manageAttachmentType.ManageAttachmentTypeCodeSizeRule;
import com.kynsoft.finamer.creditcard.domain.rules.manageAttachmentType.ManageAttachmentTypeDefaultMustBeUniqueRule;
import com.kynsoft.finamer.creditcard.domain.rules.manageAttachmentType.ManageAttachmentTypeNameMustBeNullRule;
import com.kynsoft.finamer.creditcard.domain.services.IManageAttachmentTypeService;
import org.springframework.stereotype.Component;

@Component
public class CreateManageAttachmentTypeCommandHandler implements ICommandHandler<CreateManageAttachmentTypeCommand> {

    private final IManageAttachmentTypeService service;

    public CreateManageAttachmentTypeCommandHandler(IManageAttachmentTypeService service) {
        this.service = service;
    }

    @Override
    public void handle(CreateManageAttachmentTypeCommand command) {
        RulesChecker.checkRule(new ManageAttachmentTypeCodeSizeRule(command.getCode()));
        RulesChecker.checkRule(new ManageAttachmentTypeNameMustBeNullRule(command.getName()));
        RulesChecker.checkRule(new ManageAttachmentTypeCodeMustBeUniqueRule(this.service, command.getCode(), command.getId()));

        if(command.isDefaults()) {
            RulesChecker.checkRule(new ManageAttachmentTypeDefaultMustBeUniqueRule(this.service, command.getId()));
        }

        service.create(new ManageAttachmentTypeDto(
                command.getId(),
                command.getCode(),
                command.getDescription(),
                command.getName(),
                command.getStatus(),
                command.isDefaults()
        ));
    }
}

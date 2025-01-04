package com.kynsoft.finamer.payment.application.command.attachmentType.create;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.payment.domain.dto.AttachmentTypeDto;
import com.kynsoft.finamer.payment.domain.rules.attachmentType.AttachmentTypeAntiToIncomeImportMustBeUniqueRule;
import com.kynsoft.finamer.payment.domain.rules.attachmentType.AttachmentTypeCodeMustBeUniqueRule;
import com.kynsoft.finamer.payment.domain.rules.attachmentType.AttachmentTypeDefaultMustBeUniqueRule;
import com.kynsoft.finamer.payment.domain.services.IManageAttachmentTypeService;
import org.springframework.stereotype.Component;

@Component
public class CreateAttachmentTypeCommandHandler implements ICommandHandler<CreateAttachmentTypeCommand> {

    private final IManageAttachmentTypeService service;

    public CreateAttachmentTypeCommandHandler(IManageAttachmentTypeService service) {
        this.service = service;
    }

    @Override
    public void handle(CreateAttachmentTypeCommand command) {

        RulesChecker.checkRule(new AttachmentTypeCodeMustBeUniqueRule(this.service, command.getCode(), command.getId()));
        if (command.getDefaults()) {
            RulesChecker.checkRule(new AttachmentTypeDefaultMustBeUniqueRule(this.service, command.getId()));
        }
        if (command.isAntiToIncomeImport()) {
            RulesChecker.checkRule(new AttachmentTypeAntiToIncomeImportMustBeUniqueRule(this.service, command.getId()));
        }


        service.create(new AttachmentTypeDto(
                command.getId(),
                command.getCode(),
                command.getName(),
                command.getDescription(),
                command.getDefaults(),
                command.getStatus(),
                command.isAntiToIncomeImport()
        ));
    }
}

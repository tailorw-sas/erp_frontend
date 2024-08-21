package com.kynsoft.finamer.payment.application.command.attachmentType.update;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.rules.ValidateObjectNotNullRule;
import com.kynsof.share.utils.ConsumerUpdate;
import com.kynsof.share.utils.UpdateIfNotNull;
import com.kynsoft.finamer.payment.domain.dto.AttachmentTypeDto;
import com.kynsoft.finamer.payment.domain.dtoEnum.Status;
import com.kynsoft.finamer.payment.domain.rules.attachmentType.AttachmentTypeAntiToIncomeImportMustBeUniqueRule;
import com.kynsoft.finamer.payment.domain.rules.attachmentType.AttachmentTypeDefaultMustBeUniqueRule;
import com.kynsoft.finamer.payment.domain.services.IManageAttachmentTypeService;

import java.util.function.Consumer;

import org.springframework.stereotype.Component;

@Component
public class UpdateAttachmentTypeCommandHandler implements ICommandHandler<UpdateAttachmentTypeCommand> {

    private final IManageAttachmentTypeService service;

    public UpdateAttachmentTypeCommandHandler(IManageAttachmentTypeService service) {
        this.service = service;
    }

    @Override
    public void handle(UpdateAttachmentTypeCommand command) {

        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(command.getId(), "id", "Attachment Type ID cannot be null."));

        AttachmentTypeDto attachmentTypeDto = this.service.findById(command.getId());
        if (command.getDefaults()) {
            RulesChecker.checkRule(new AttachmentTypeDefaultMustBeUniqueRule(this.service, command.getId()));
        }
        if (command.isAntiToIncomeImport()) {
            RulesChecker.checkRule(new AttachmentTypeAntiToIncomeImportMustBeUniqueRule(this.service, command.getId()));
        }


        ConsumerUpdate update = new ConsumerUpdate();
        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(attachmentTypeDto::setDescription, command.getDescription(), attachmentTypeDto.getDescription(), update::setUpdate);
        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(attachmentTypeDto::setName, command.getName(), attachmentTypeDto.getName(), update::setUpdate);
        this.updateStatus(attachmentTypeDto::setStatus, command.getStatus(), attachmentTypeDto.getStatus(), update::setUpdate);
        this.updateDefault(attachmentTypeDto::setDefaults, command.getDefaults(), attachmentTypeDto.getDefaults(), update::setUpdate);
        this.updateDefault(attachmentTypeDto::setAntiToIncomeImport, command.isAntiToIncomeImport(),
                attachmentTypeDto.isAntiToIncomeImport(), update::setUpdate);

        if (update.getUpdate() > 0) {
            this.service.update(attachmentTypeDto);
        }

    }

    private void updateStatus(Consumer<Status> setter, Status newValue, Status oldValue, Consumer<Integer> update) {
        if (newValue != null && !newValue.equals(oldValue)) {
            setter.accept(newValue);
            update.accept(1);
        }
    }

    private void updateDefault(Consumer<Boolean> setter, Boolean newValue, Boolean oldValue, Consumer<Integer> update) {
        if (newValue != null && !newValue.equals(oldValue)) {
            setter.accept(newValue);
            update.accept(1);
        }
    }

}

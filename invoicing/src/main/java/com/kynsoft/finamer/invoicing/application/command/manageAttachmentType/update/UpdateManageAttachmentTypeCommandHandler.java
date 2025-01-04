package com.kynsoft.finamer.invoicing.application.command.manageAttachmentType.update;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.rules.ValidateObjectNotNullRule;
import com.kynsof.share.utils.ConsumerUpdate;
import com.kynsof.share.utils.UpdateIfNotNull;
import com.kynsoft.finamer.invoicing.domain.dto.ManageAttachmentTypeDto;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.Status;
import com.kynsoft.finamer.invoicing.domain.services.IManageAttachmentTypeService;
import org.springframework.stereotype.Component;

@Component
public class UpdateManageAttachmentTypeCommandHandler implements ICommandHandler<UpdateManageAttachmentTypeCommand> {

    private final IManageAttachmentTypeService service;

    public UpdateManageAttachmentTypeCommandHandler(IManageAttachmentTypeService service) {
        this.service = service;
    }

    @Override
    public void handle(UpdateManageAttachmentTypeCommand command) {
        RulesChecker.checkRule(
                new ValidateObjectNotNullRule<>(command.getId(), "id", "Manage Attachment Type ID cannot be null."));

        ManageAttachmentTypeDto dto = service.findById(command.getId());

        ConsumerUpdate update = new ConsumerUpdate();

        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(dto::setName, command.getName(), dto.getName(),
                update::setUpdate);
        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(Status::valueOf, command.getStatus(), dto.getStatus().name(), update::setUpdate);
        UpdateIfNotNull.updateBoolean(dto::setDefaults, command.getDefaults(), dto.getDefaults(), update::setUpdate);
        UpdateIfNotNull.updateBoolean(dto::setAttachInvDefault, command.isAttachInvDefault(), dto.isAttachInvDefault(), update::setUpdate);


        if (update.getUpdate() > 0) {
            this.service.update(dto);
        }
    }

}

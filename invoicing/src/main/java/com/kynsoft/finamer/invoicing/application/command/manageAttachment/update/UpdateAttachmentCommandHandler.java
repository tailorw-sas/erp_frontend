package com.kynsoft.finamer.invoicing.application.command.manageAttachment.update;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.utils.ConsumerUpdate;
import com.kynsof.share.utils.UpdateIfNotNull;
import com.kynsoft.finamer.invoicing.domain.dto.ManageAttachmentDto;
import com.kynsoft.finamer.invoicing.domain.dto.ManageAttachmentTypeDto;

import com.kynsoft.finamer.invoicing.domain.services.IManageAttachmentService;
import com.kynsoft.finamer.invoicing.domain.services.IManageAttachmentTypeService;

import com.kynsoft.finamer.invoicing.domain.services.IManageRoomRateService;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.function.Consumer;

@Component
public class UpdateAttachmentCommandHandler implements ICommandHandler<UpdateAttachmentCommand> {

    private final IManageAttachmentService attachmentService;
    private final IManageAttachmentTypeService attachmentTypeService;

    public UpdateAttachmentCommandHandler(IManageAttachmentService attachmentService,
            IManageAttachmentTypeService attachmentTypeService, IManageRoomRateService roomRateService) {
        this.attachmentService = attachmentService;
        this.attachmentTypeService = attachmentTypeService;
    }

    @Override
    public void handle(UpdateAttachmentCommand command) {
        ManageAttachmentDto dto = this.attachmentService.findById(command.getId());

        ConsumerUpdate update = new ConsumerUpdate();

        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(dto::setFile, command.getFile(), dto.getFile(),
                update::setUpdate);
        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(dto::setRemark, command.getRemark(), dto.getRemark(),
                update::setUpdate);
        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(dto::setFilename, command.getFilename(),
                dto.getFilename(), update::setUpdate);

        if (command.getType() != null) {

            this.updateType(dto::setType, command.getType(), dto.getType().getId(), update::setUpdate);
        }

        if (update.getUpdate() > 0) {
            this.attachmentService.update(dto);
        }
    }

    public void updateType(Consumer<ManageAttachmentTypeDto> setter, UUID newValue, UUID oldValue,
            Consumer<Integer> update) {
        if (newValue != null && !newValue.equals(oldValue)) {
            ManageAttachmentTypeDto attachmentTypeDto = this.attachmentTypeService.findById(newValue);
            setter.accept(attachmentTypeDto);
            update.accept(1);
        }
    }
}

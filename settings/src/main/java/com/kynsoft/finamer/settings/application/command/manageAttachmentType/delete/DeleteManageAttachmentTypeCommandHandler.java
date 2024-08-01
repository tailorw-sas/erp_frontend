package com.kynsoft.finamer.settings.application.command.manageAttachmentType.delete;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.kafka.entity.ObjectIdKafka;
import com.kynsoft.finamer.settings.domain.dto.ManageAttachmentTypeDto;
import com.kynsoft.finamer.settings.domain.services.IManageAttachmentTypeService;
import com.kynsoft.finamer.settings.infrastructure.services.kafka.producer.manageAttachmentType.ProducerDeleteManageAttachmentTypeService;
import org.springframework.stereotype.Component;

@Component
public class DeleteManageAttachmentTypeCommandHandler implements ICommandHandler<DeleteManageAttachmentTypeCommand> {

    private final IManageAttachmentTypeService service;

    private final ProducerDeleteManageAttachmentTypeService producerService;

    public DeleteManageAttachmentTypeCommandHandler(IManageAttachmentTypeService service, ProducerDeleteManageAttachmentTypeService producerService) {
        this.service = service;
        this.producerService = producerService;
    }

    @Override
    public void handle(DeleteManageAttachmentTypeCommand command) {
        ManageAttachmentTypeDto dto = service.findById(command.getId());

        service.delete(dto);
        this.producerService.delete(new ObjectIdKafka(command.getId()));
    }
}

package com.kynsoft.finamer.settings.application.command.manageVCCTransactionType.delete;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.kafka.entity.ObjectIdKafka;
import com.kynsoft.finamer.settings.domain.dto.ManageVCCTransactionTypeDto;
import com.kynsoft.finamer.settings.domain.services.IManageVCCTransactionTypeService;
import com.kynsoft.finamer.settings.infrastructure.services.kafka.producer.manageVCCTransactionType.ProducerDeleteManageVCCTransactionTypeService;
import org.springframework.stereotype.Component;

@Component
public class DeleteManageVCCTransactionTypeCommandHandler implements ICommandHandler<DeleteManageVCCTransactionTypeCommand> {

    private final IManageVCCTransactionTypeService service;

    private final ProducerDeleteManageVCCTransactionTypeService producer;

    public DeleteManageVCCTransactionTypeCommandHandler(IManageVCCTransactionTypeService service, ProducerDeleteManageVCCTransactionTypeService producer) {
        this.service = service;
        this.producer = producer;
    }

    @Override
    public void handle(DeleteManageVCCTransactionTypeCommand command) {
        ManageVCCTransactionTypeDto dto = service.findById(command.getId());

        service.delete(dto);
        producer.delete(new ObjectIdKafka(command.getId()));
    }
}

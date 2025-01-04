package com.kynsoft.finamer.settings.application.command.manageInvoiceType.delete;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.kafka.entity.ReplicateDeleteManageInvoiceTypeKafka;
import com.kynsoft.finamer.settings.domain.dto.ManageInvoiceTypeDto;
import com.kynsoft.finamer.settings.domain.services.IManageInvoiceTypeService;
import com.kynsoft.finamer.settings.infrastructure.services.kafka.producer.manageInvoiceType.ProducerDeleteManageInvoiceTypeService;
import org.springframework.stereotype.Component;

@Component
public class DeleteManageInvoiceTypeCommandHandler implements ICommandHandler<DeleteManageInvoiceTypeCommand> {

    private final IManageInvoiceTypeService service;
    private final ProducerDeleteManageInvoiceTypeService producer;

    public DeleteManageInvoiceTypeCommandHandler(IManageInvoiceTypeService service, ProducerDeleteManageInvoiceTypeService producer) {
        this.service = service;
        this.producer = producer;
    }

    @Override
    public void handle(DeleteManageInvoiceTypeCommand command) {
        ManageInvoiceTypeDto dto = service.findById(command.getId());

        service.delete(dto);
        this.producer.delete(new ReplicateDeleteManageInvoiceTypeKafka(dto.getId()));
    }
}

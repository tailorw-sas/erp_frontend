package com.kynsoft.finamer.settings.application.command.manageInvoiceTransactionType.delete;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.kafka.entity.ObjectIdKafka;
import com.kynsoft.finamer.settings.domain.dto.ManageInvoiceTransactionTypeDto;
import com.kynsoft.finamer.settings.domain.services.IManageInvoiceTransactionTypeService;
import com.kynsoft.finamer.settings.infrastructure.services.kafka.producer.manageInvoiceTransactionType.ProducerDeleteManageInvoiceTransactionTypeService;
import org.springframework.stereotype.Component;

@Component
public class DeleteManageInvoiceTransactionTypeCommandHandler implements ICommandHandler<DeleteManageInvoiceTransactionTypeCommand> {

    private final IManageInvoiceTransactionTypeService service;
    private final ProducerDeleteManageInvoiceTransactionTypeService producer;

    public DeleteManageInvoiceTransactionTypeCommandHandler(IManageInvoiceTransactionTypeService service, ProducerDeleteManageInvoiceTransactionTypeService producer) {
        this.service = service;
        this.producer = producer;
    }

    @Override
    public void handle(DeleteManageInvoiceTransactionTypeCommand command) {
        ManageInvoiceTransactionTypeDto dto = service.findById(command.getId());

        service.delete(dto);
        this.producer.delete(new ObjectIdKafka(command.getId()));
    }
}

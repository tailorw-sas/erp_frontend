package com.kynsoft.finamer.settings.application.command.manageInvoiceStatus.delete;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.kafka.entity.ReplicateDeleteManageInvoiceStatusKafka;
import com.kynsoft.finamer.settings.domain.dto.ManageInvoiceStatusDto;
import com.kynsoft.finamer.settings.domain.services.IManageInvoiceStatusService;
import com.kynsoft.finamer.settings.infrastructure.services.kafka.producer.manageInvoiceStatus.ProducerDeleteManageInvoiceStatusService;
import org.springframework.stereotype.Component;

@Component
public class DeleteManageInvoiceStatusCommandHandler implements ICommandHandler<DeleteManageInvoiceStatusCommand> {

    private final IManageInvoiceStatusService service;
    private final ProducerDeleteManageInvoiceStatusService producer;

    public DeleteManageInvoiceStatusCommandHandler(IManageInvoiceStatusService service, ProducerDeleteManageInvoiceStatusService producer) {
        this.service = service;
        this.producer = producer;
    }

    @Override
    public void handle(DeleteManageInvoiceStatusCommand command) {
        ManageInvoiceStatusDto dto = this.service.findById(command.getId());

        this.service.delete(dto);
        this.producer.delete(new ReplicateDeleteManageInvoiceStatusKafka(dto.getId()));
    }
}

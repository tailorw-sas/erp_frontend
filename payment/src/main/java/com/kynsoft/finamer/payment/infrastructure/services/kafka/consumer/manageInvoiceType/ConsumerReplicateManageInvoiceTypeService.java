package com.kynsoft.finamer.payment.infrastructure.services.kafka.consumer.manageInvoiceType;

import com.kynsof.share.core.domain.kafka.entity.ReplicateManageInvoiceTypeKafka;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.payment.application.command.manageInvoiceType.create.CreateManageInvoiceTypeCommand;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ConsumerReplicateManageInvoiceTypeService {

    private final IMediator mediator;

    public ConsumerReplicateManageInvoiceTypeService(IMediator mediator) {

        this.mediator = mediator;
    }

    @KafkaListener(topics = "finamer-replicate-manage-invoice-type", groupId = "payment-entity-replica")
    public void listen(ReplicateManageInvoiceTypeKafka objKafka) {
        try {

            CreateManageInvoiceTypeCommand command = new CreateManageInvoiceTypeCommand(objKafka.getId(),
                    objKafka.getCode(), objKafka.getName());
            mediator.send(command);
        } catch (Exception ex) {
            Logger.getLogger(ConsumerReplicateManageInvoiceTypeService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}

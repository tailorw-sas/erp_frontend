package com.kynsoft.finamer.invoicing.infrastructure.services.kafka.consumer.manageInvoiceType;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kynsof.share.core.domain.kafka.entity.ReplicateManageInvoiceTypeKafka;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.invoicing.application.command.manageInvoiceType.create.CreateManageInvoiceTypeCommand;
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

    @KafkaListener(topics = "finamer-replicate-manage-invoice-type", groupId = "invoicing-entity-replica")
    public void listen(String event) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(event);

            ReplicateManageInvoiceTypeKafka objKafka = objectMapper.treeToValue(rootNode, ReplicateManageInvoiceTypeKafka.class);
            CreateManageInvoiceTypeCommand command = new CreateManageInvoiceTypeCommand(objKafka.getId(), objKafka.getCode(), objKafka.getName());
            mediator.send(command);
        } catch (JsonProcessingException ex) {
            Logger.getLogger(ConsumerReplicateManageInvoiceTypeService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }


}

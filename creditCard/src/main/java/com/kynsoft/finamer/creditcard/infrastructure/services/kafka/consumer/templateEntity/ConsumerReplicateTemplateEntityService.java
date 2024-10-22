package com.kynsoft.finamer.creditcard.infrastructure.services.kafka.consumer.templateEntity;

import com.kynsof.share.core.domain.kafka.entity.TemplateKafka;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.creditcard.application.command.templateEntity.create.CreateTemplateEntityCommand;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ConsumerReplicateTemplateEntityService {

    private final IMediator mediator;

    public ConsumerReplicateTemplateEntityService(IMediator mediator) {
        this.mediator = mediator;
    }

    @KafkaListener(topics = "finamer-replicate-template-entity", groupId = "vcc-entity-replica")
    public void listen(TemplateKafka entity) {
        try {
            CreateTemplateEntityCommand command = new CreateTemplateEntityCommand(
                    entity.getId(), entity.getTemplateCode(), entity.getName(),
                    entity.getLanguageCode(), entity.getType());
            mediator.send(command);
        } catch (Exception ex) {
            Logger.getLogger(ConsumerReplicateTemplateEntityService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}

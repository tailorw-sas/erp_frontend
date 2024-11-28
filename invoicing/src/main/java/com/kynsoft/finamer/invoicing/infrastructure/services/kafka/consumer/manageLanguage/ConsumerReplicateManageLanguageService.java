package com.kynsoft.finamer.invoicing.infrastructure.services.kafka.consumer.manageLanguage;

import com.kynsof.share.core.domain.kafka.entity.vcc.ReplicateManageLanguageKafka;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.invoicing.application.command.manageLanguage.create.CreateManageLanguageCommand;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ConsumerReplicateManageLanguageService {

    private final IMediator mediator;

    public ConsumerReplicateManageLanguageService(IMediator mediator) {
        this.mediator = mediator;
    }

    @KafkaListener(topics = "finamer-replicate-manage-language", groupId = "invoicing-entity-replica")
    public void listen(ReplicateManageLanguageKafka entity) {
        try {
            CreateManageLanguageCommand command = new CreateManageLanguageCommand(entity.getId(), entity.getCode(), entity.getName(), entity.getDefaults(), entity.getStatus());
            mediator.send(command);
        } catch (Exception ex) {
            Logger.getLogger(ConsumerReplicateManageLanguageService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}

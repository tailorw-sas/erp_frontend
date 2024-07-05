package com.kynsoft.finamer.creditcard.infrastructure.services.kafka.consumer.manageLanguage;

import com.kynsof.share.core.domain.kafka.entity.vcc.ReplicateManageLanguageKafka;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.creditcard.application.command.manageLanguage.create.CreateManageLanguageCommand;
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

    @KafkaListener(topics = "finamer-replicate-manage-language", groupId = "vcc-entity-replica")
    public void listen(ReplicateManageLanguageKafka entity) {
        try {
            CreateManageLanguageCommand command = new CreateManageLanguageCommand(entity.getId(), entity.getCode(), entity.getName());
            mediator.send(command);
        } catch (Exception ex) {
            Logger.getLogger(ConsumerReplicateManageLanguageService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}

package com.kynsoft.finamer.invoicing.infrastructure.services.kafka.consumer.manageClient;

import com.kynsof.share.core.domain.kafka.entity.update.UpdateManageClientKafka;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.invoicing.application.command.manageClient.update.UpdateManageClientCommand;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ConsumerUpdateManageClientService {

    private final IMediator mediator;

    public ConsumerUpdateManageClientService(IMediator mediator) {
        this.mediator = mediator;
    }

    @KafkaListener(topics = "finamer-update-manage-client", groupId = "invoicing-entity-replica")
    public void listen(UpdateManageClientKafka objKafka) {
        try {
            UpdateManageClientCommand command = new UpdateManageClientCommand(objKafka.getId(), objKafka.getName(), objKafka.getIsNightType(), objKafka.getStatus());
            mediator.send(command);
        } catch (Exception ex) {
            Logger.getLogger(ConsumerUpdateManageClientService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}

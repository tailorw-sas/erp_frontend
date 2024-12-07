package com.kynsoft.finamer.invoicing.infrastructure.services.kafka.consumer.manageCitySite;

import com.kynsof.share.core.domain.kafka.entity.ReplicateManageCityStateKafka;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.invoicing.application.command.manageCityState.create.CreateManageCityStateCommand;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.Status;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ConsumerReplicateManageCitySiteService {

    private final IMediator mediator;

    public ConsumerReplicateManageCitySiteService(IMediator mediator) {

        this.mediator = mediator;
    }

    @KafkaListener(topics = "finamer-replicate-manage-city-state", groupId = "invoicing-entity-replica")
    public void listen(ReplicateManageCityStateKafka objKafka) {
        try {
            CreateManageCityStateCommand command = new CreateManageCityStateCommand(objKafka.getId(),objKafka.getCode(),
                    objKafka.getName(),objKafka.getDescription(), Status.valueOf(objKafka.getStatus()),objKafka.getCountry(),objKafka.getTimeZone());
            mediator.send(command);
        } catch (Exception ex) {
            Logger.getLogger(ConsumerReplicateManageCitySiteService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}

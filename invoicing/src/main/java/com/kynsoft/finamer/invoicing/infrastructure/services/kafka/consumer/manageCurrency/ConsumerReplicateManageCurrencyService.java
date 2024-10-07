package com.kynsoft.finamer.invoicing.infrastructure.services.kafka.consumer.manageCurrency;

import com.kynsof.share.core.domain.kafka.entity.ReplicateManageCurrencyKafka;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.invoicing.application.command.manageCurrency.create.CreateManageCurrencyCommand;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ConsumerReplicateManageCurrencyService {

    private final IMediator mediator;

    public ConsumerReplicateManageCurrencyService(IMediator mediator) {

        this.mediator = mediator;
    }

    @KafkaListener(topics = "finamer-replicate-manage-currency", groupId = "invoicing-entity-replica")
    public void listen(ReplicateManageCurrencyKafka objKafka) {
        try {
//
            CreateManageCurrencyCommand command = new CreateManageCurrencyCommand(
                    objKafka.getId(),
                    objKafka.getCode(),
                    objKafka.getName(),
                    objKafka.getStatus()
            );
            mediator.send(command);
        } catch (Exception ex) {
            Logger.getLogger(ConsumerReplicateManageCurrencyService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}

package com.kynsoft.finamer.invoicing.infrastructure.services.kafka.consumer.manageCurrency;

import com.kynsof.share.core.domain.kafka.entity.ReplicateManageCurrencyKafka;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.invoicing.application.command.manageCurrency.update.UpdateManageCurrencyCommand;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ConsumerUpdateManageCurrencyService {

    private final IMediator mediator;

    public ConsumerUpdateManageCurrencyService(IMediator mediator) {
        this.mediator = mediator;
    }

    @KafkaListener(topics = "finamer-update-manage-currency", groupId = "invoicing-entity-replica")
    public void listen(ReplicateManageCurrencyKafka objKafka) {
        try {
            UpdateManageCurrencyCommand command = new UpdateManageCurrencyCommand(
                    objKafka.getId(),
                    objKafka.getName(),
                    objKafka.getStatus()
            );
            mediator.send(command);
        } catch (Exception ex) {
            Logger.getLogger(ConsumerUpdateManageCurrencyService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}

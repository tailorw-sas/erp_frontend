package com.kynsoft.finamer.creditcard.infrastructure.services.kafka.consumer.manageCurrency;

import com.kynsof.share.core.domain.kafka.entity.ReplicateManageCurrencyKafka;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.creditcard.application.command.manageCurrency.update.UpdateManagerCurrencyCommand;
import com.kynsoft.finamer.creditcard.domain.dtoEnum.Status;
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

    @KafkaListener(topics = "finamer-update-manage-currency", groupId = "vcc-entity-replica")
    public void listen(ReplicateManageCurrencyKafka objKafka) {
        try {
            UpdateManagerCurrencyCommand command = new UpdateManagerCurrencyCommand(
                    objKafka.getId(),
                    objKafka.getName(),
                    Status.valueOf(objKafka.getStatus())
            );
            mediator.send(command);
        } catch (Exception ex) {
            Logger.getLogger(ConsumerUpdateManageCurrencyService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}

package com.kynsoft.finamer.creditcard.infrastructure.services.kafka.consumer.manageCurrency;

import com.kynsof.share.core.domain.kafka.entity.ReplicateManageCurrencyKafka;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.creditcard.application.command.manageCurrency.create.CreateManagerCurrencyCommand;
import com.kynsoft.finamer.creditcard.domain.dtoEnum.Status;
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

    @KafkaListener(topics = "finamer-replicate-manage-currency", groupId = "vcc-entity-replica")
    public void listen(ReplicateManageCurrencyKafka objKafka) {
        try {
//
            CreateManagerCurrencyCommand command = new CreateManagerCurrencyCommand(
                    objKafka.getId(),
                    objKafka.getCode(),
                    objKafka.getName(),
                    Status.valueOf(objKafka.getStatus())
            );
            mediator.send(command);
        } catch (Exception ex) {
            Logger.getLogger(ConsumerReplicateManageCurrencyService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}

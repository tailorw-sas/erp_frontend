package com.kynsoft.finamer.creditcard.infrastructure.services.kafka.consumer.manageBank;

import com.kynsof.share.core.domain.kafka.entity.ManageBankKafka;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.creditcard.application.command.manageBank.create.CreateManagerBankCommand;
import com.kynsoft.finamer.creditcard.domain.dtoEnum.Status;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ConsumerReplicateBankService {

    private final IMediator mediator;

    public ConsumerReplicateBankService(IMediator mediator) {

        this.mediator = mediator;
    }

    @KafkaListener(topics = "finamer-replicate-manage-bank", groupId = "vcc-entity-replica")
    public void listen(ManageBankKafka objKafka) {
        try {
            CreateManagerBankCommand command =new CreateManagerBankCommand(
                    objKafka.getId(),objKafka.getCode(), objKafka.getName(),
                    objKafka.getDescription(), Status.valueOf(objKafka.getStatus()));
            mediator.send(command);
        } catch (Exception ex) {
            Logger.getLogger(ConsumerReplicateBankService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}

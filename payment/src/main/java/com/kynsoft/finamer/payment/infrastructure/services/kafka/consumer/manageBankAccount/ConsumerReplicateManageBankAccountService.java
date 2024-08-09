package com.kynsoft.finamer.payment.infrastructure.services.kafka.consumer.manageBankAccount;

import com.kynsof.share.core.domain.kafka.entity.ReplicateManageBankAccountKafka;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.payment.application.command.manageBankAccount.create.CreateManageBankAccountCommand;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ConsumerReplicateManageBankAccountService {

    private final IMediator mediator;

    public ConsumerReplicateManageBankAccountService(IMediator mediator) {

        this.mediator = mediator;
    }

    @KafkaListener(topics = "finamer-replicate-manage-bank-account", groupId = "payment-entity-replica")
    public void listen(ReplicateManageBankAccountKafka objKafka) {
        try {
            CreateManageBankAccountCommand command = new CreateManageBankAccountCommand(objKafka.getId(), objKafka.getAccountNumber(), objKafka.getStatus(), objKafka.getNameOfBank(),objKafka.getManageHotel());
            mediator.send(command);
        } catch (Exception ex) {
            Logger.getLogger(ConsumerReplicateManageBankAccountService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}

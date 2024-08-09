package com.kynsoft.finamer.payment.infrastructure.services.kafka.consumer.manageBankAccount;

import com.kynsof.share.core.domain.kafka.entity.update.UpdateManageBankAccountKafka;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.payment.application.command.manageBankAccount.update.UpdateManageBankAccountCommand;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ConsumerUpdateManageBankAccountService {

    private final IMediator mediator;

    public ConsumerUpdateManageBankAccountService(IMediator mediator) {

        this.mediator = mediator;
    }

    @KafkaListener(topics = "finamer-update-manage-bank-account", groupId = "payment-entity-replica")
    public void listen(UpdateManageBankAccountKafka objKafka) {
        try {
            UpdateManageBankAccountCommand command = new UpdateManageBankAccountCommand(objKafka.getId(), objKafka.getAccountNumber(), objKafka.getStatus(), objKafka.getNameOfBank(),objKafka.getManageHotel());
            mediator.send(command);
        } catch (Exception ex) {
            Logger.getLogger(ConsumerUpdateManageBankAccountService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}

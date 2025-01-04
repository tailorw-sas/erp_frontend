package com.kynsoft.finamer.creditcard.infrastructure.services.kafka.consumer.manageBankAccount;

import com.kynsof.share.core.domain.kafka.entity.update.UpdateManageBankAccountKafka;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.creditcard.application.command.manageBankAccount.update.UpdateManageBankAccountCommand;
import com.kynsoft.finamer.creditcard.domain.dtoEnum.Status;
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

    @KafkaListener(topics = "finamer-update-manage-bank-account", groupId = "vcc-entity-replica")
    public void listen(UpdateManageBankAccountKafka objKafka) {
        try {
            UpdateManageBankAccountCommand command = new UpdateManageBankAccountCommand(
                    objKafka.getId(), Status.valueOf(objKafka.getStatus()), objKafka.getManageBank(),
                    objKafka.getManageHotel(), objKafka.getManageAccountType(), objKafka.getDescription()
            );
            mediator.send(command);
        } catch (Exception ex) {
            Logger.getLogger(ConsumerUpdateManageBankAccountService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}

package com.kynsoft.finamer.creditcard.infrastructure.services.kafka.consumer.manageAccountType;

import com.kynsof.share.core.domain.kafka.entity.ManageAccountTypeKafka;
import com.kynsof.share.core.domain.kafka.entity.ManageBankKafka;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.creditcard.application.command.manageAccountType.create.CreateManagerAccountTypeCommand;
import com.kynsoft.finamer.creditcard.application.command.manageBank.create.CreateManagerBankCommand;
import com.kynsoft.finamer.creditcard.domain.dtoEnum.Status;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ConsumerReplicateAccountTypeService {

    private final IMediator mediator;

    public ConsumerReplicateAccountTypeService(IMediator mediator) {

        this.mediator = mediator;
    }

    @KafkaListener(topics = "finamer-replicate-manage-account-type", groupId = "vcc-entity-replica")
    public void listen(ManageAccountTypeKafka objKafka) {
        try {
            CreateManagerAccountTypeCommand command =new CreateManagerAccountTypeCommand(
                    objKafka.getId(),objKafka.getCode(), objKafka.getName(),
                    objKafka.getDescription(), Status.valueOf(objKafka.getStatus()),
                    objKafka.isModuleVcc(), objKafka.isModulePayment());
            mediator.send(command);
        } catch (Exception ex) {
            Logger.getLogger(ConsumerReplicateAccountTypeService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}

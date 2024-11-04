package com.kynsoft.finamer.creditcard.infrastructure.services.kafka.consumer.manageMerchantBankAccount;

import com.kynsof.share.core.domain.kafka.entity.ManageBankKafka;
import com.kynsof.share.core.domain.kafka.entity.ManageMerchantBankAccountKafka;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.creditcard.application.command.manageBank.create.CreateManagerBankCommand;
import com.kynsoft.finamer.creditcard.application.command.manageMerchantBankAccount.create.CreateManageMerchantBankAccountCommand;
import com.kynsoft.finamer.creditcard.domain.dtoEnum.Status;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ConsumerReplicateManageMerchantBankAccountService {

    private final IMediator mediator;

    public ConsumerReplicateManageMerchantBankAccountService(IMediator mediator) {

        this.mediator = mediator;
    }

    @KafkaListener(topics = "finamer-replicate-manage-merchant-bank-account", groupId = "vcc-entity-replica")
    public void listen(ManageMerchantBankAccountKafka objKafka) {
        try {
//            CreateManageMerchantBankAccountCommand command =new CreateManageMerchantBankAccountCommand(
//                    objKafka.getId(), objKafka.getManagerMerchant(), objKafka.getManageBank(),
//                    objKafka.getAccountNumber(), objKafka.getDescription(), Status.valueOf(objKafka.getStatus()),
//                    objKafka.getCreditCardTypes());
//            mediator.send(command);
        } catch (Exception ex) {
            Logger.getLogger(ConsumerReplicateManageMerchantBankAccountService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}

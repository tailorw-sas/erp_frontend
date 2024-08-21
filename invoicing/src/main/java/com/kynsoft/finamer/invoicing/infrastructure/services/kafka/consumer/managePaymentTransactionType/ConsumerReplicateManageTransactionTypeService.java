package com.kynsoft.finamer.invoicing.infrastructure.services.kafka.consumer.managePaymentTransactionType;

import com.kynsof.share.core.domain.kafka.entity.ReplicateManagePaymentTransactionTypeKafka;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.invoicing.application.command.managePaymentTransactionType.create.CreateManagePaymentTransactionTypeCommand;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ConsumerReplicateManageTransactionTypeService {

    private final IMediator mediator;

    public ConsumerReplicateManageTransactionTypeService(IMediator mediator) {
        this.mediator = mediator;
    }

    @KafkaListener(topics = "finamer-replicate-manage-payment-transaction-type", groupId = "invoicing-entity-replica")
    public void listen(ReplicateManagePaymentTransactionTypeKafka objKafka) {
        try {
            CreateManagePaymentTransactionTypeCommand command = new CreateManagePaymentTransactionTypeCommand(
                    objKafka.getId(), 
                    objKafka.getCode(), 
                    objKafka.getName(),
                    objKafka.getStatus(),
                    objKafka.getCash(),
                    objKafka.getDeposit(),
                    objKafka.getApplyDeposit(),
                    objKafka.getRemarkRequired(),
                    objKafka.getMinNumberOfCharacter(),
                    objKafka.getDefaultRemark(),
                    objKafka.getDefaults()
            );
            mediator.send(command);
        } catch (Exception ex) {
            Logger.getLogger(ConsumerReplicateManageTransactionTypeService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}

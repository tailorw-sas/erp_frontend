package com.kynsoft.finamer.creditcard.infrastructure.services.kafka.consumer.manageMerchant;

import com.kynsof.share.core.domain.kafka.entity.vcc.ReplicateManageMerchantKafka;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.creditcard.application.command.manageMerchant.create.CreateManageMerchantCommand;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ConsumerReplicateManageMerchantService {

    private final IMediator mediator;

    public ConsumerReplicateManageMerchantService(IMediator mediator) {
        this.mediator = mediator;
    }

    @KafkaListener(topics = "finamer-replicate-manage-merchant", groupId = "vcc-entity-replica")
    public void listen(ReplicateManageMerchantKafka entity) {
        try {
            CreateManageMerchantCommand command = new CreateManageMerchantCommand(entity.getId(), entity.getCode(),entity.getDescription(),entity.getB2bPartner(),entity.getDefaultm(), entity.getStatus());
            mediator.send(command);
        } catch (Exception ex) {
            Logger.getLogger(com.kynsoft.finamer.creditcard.infrastructure.services.kafka.consumer.manageMerchant.ConsumerReplicateManageMerchantService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}

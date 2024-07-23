package com.kynsoft.finamer.creditcard.infrastructure.services.kafka.consumer.manageMerchantCommission;

import com.kynsof.share.core.domain.kafka.entity.vcc.ReplicateManageMerchantCommissionKafka;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.creditcard.application.command.manageMerchantCommission.create.CreateManageMerchantCommissionCommand;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ConsumerReplicateManageMerchantCommissionService {

    private final IMediator mediator;

    public ConsumerReplicateManageMerchantCommissionService(IMediator mediator) {
        this.mediator = mediator;
    }

    @KafkaListener(topics = "finamer-replicate-manage-merchant-commission", groupId = "vcc-entity-replica")
    public void listen(ReplicateManageMerchantCommissionKafka entity) {
        try {
            CreateManageMerchantCommissionCommand command = new CreateManageMerchantCommissionCommand(
                    entity.getId(),
                    entity.getManagerMerchant(),
                    entity.getManageCreditCartType(),
                    entity.getCommission(),
                    entity.getCalculationType(),
                    entity.getFromDate(),
                    entity.getToDate(),
                    entity.getStatus());
            mediator.send(command);
        } catch (Exception ex) {
            Logger.getLogger(ConsumerReplicateManageMerchantCommissionService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}

package com.kynsoft.finamer.payment.infrastructure.services.kafka.consumer.manageAgencyType;

import com.kynsof.share.core.domain.kafka.entity.ReplicateManageAgencyTypeKafka;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.payment.application.command.manageAgencyType.create.CreateManageAgencyTypeCommand;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ConsumerReplicateManageAgencyTypeService {

    private final IMediator mediator;

    public ConsumerReplicateManageAgencyTypeService(IMediator mediator) {
        this.mediator = mediator;
    }

    @KafkaListener(topics = "finamer-replicate-manage-agency-type", groupId = "payment-entity-replica")
    public void listen(ReplicateManageAgencyTypeKafka objKafka) {
        try {
            CreateManageAgencyTypeCommand command = new CreateManageAgencyTypeCommand(objKafka.getId(), objKafka.getCode(), objKafka.getName(), objKafka.getStatus());
            mediator.send(command);
        } catch (Exception ex) {
            Logger.getLogger(ConsumerReplicateManageAgencyTypeService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}

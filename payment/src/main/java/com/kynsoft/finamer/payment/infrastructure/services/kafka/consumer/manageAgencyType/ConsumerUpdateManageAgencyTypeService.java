package com.kynsoft.finamer.payment.infrastructure.services.kafka.consumer.manageAgencyType;

import com.kynsof.share.core.domain.kafka.entity.update.UpdateManageAgencyTypeKafka;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.payment.application.command.manageAgencyType.update.UpdateManageAgencyTypeCommand;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ConsumerUpdateManageAgencyTypeService {

    private final IMediator mediator;

    public ConsumerUpdateManageAgencyTypeService(IMediator mediator) {
        this.mediator = mediator;
    }

    @KafkaListener(topics = "finamer-update-manage-agency-type", groupId = "payment-entity-replica")
    public void listen(UpdateManageAgencyTypeKafka objKafka) {
        try {
            UpdateManageAgencyTypeCommand command = new UpdateManageAgencyTypeCommand(objKafka.getId(), objKafka.getName(), objKafka.getStatus());
            mediator.send(command);
        } catch (Exception ex) {
            Logger.getLogger(ConsumerUpdateManageAgencyTypeService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}

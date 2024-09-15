package com.kynsoft.finamer.invoicing.infrastructure.services.kafka.consumer.manageEmployee;

import com.kynsof.share.core.domain.kafka.entity.update.UpdateManageEmployeeKafka;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.invoicing.application.command.manageEmployee.create.CreateManageEmployeeCommand;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ConsumerUpdateManageEmployeeService {

    private final IMediator mediator;

    public ConsumerUpdateManageEmployeeService(IMediator mediator) {
        this.mediator = mediator;
    }

    @KafkaListener(topics = "finamer-update-manage-employee", groupId = "payment-entity-replica")
    public void listen(UpdateManageEmployeeKafka objKafka) {
        try {
            CreateManageEmployeeCommand command = new CreateManageEmployeeCommand(objKafka.getId(), objKafka.getFirstName(), objKafka.getLastName(), objKafka.getEmail());
            mediator.send(command);
        } catch (Exception ex) {
            Logger.getLogger(ConsumerUpdateManageEmployeeService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}

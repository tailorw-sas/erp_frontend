package com.kynsoft.finamer.insis.infrastructure.services.kafka.consumer.manageEmployee;

import com.kynsof.share.core.domain.kafka.entity.ReplicateManageEmployeeKafka;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.insis.application.command.manageEmployee.create.CreateManageEmployeeCommand;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ConsumerReplicateManageEmployeeService {

    private final IMediator mediator;

    public ConsumerReplicateManageEmployeeService(IMediator mediator){
        this.mediator = mediator;
    }

    @KafkaListener(topics = "finamer-replicate-manage-employee", groupId = "innsist-entity-replica")
    public void listen(ReplicateManageEmployeeKafka entity){
        try{
            CreateManageEmployeeCommand command = new CreateManageEmployeeCommand(
                    entity.getId(),
                    entity.getFirstName(),
                    entity.getLastName(),
                    entity.getEmail(),
                    entity.getManageHotelList(),
                    entity.getManageAgencyList()
            );
            mediator.send(command);
        }catch (Exception ex){
            Logger.getLogger(ConsumerReplicateManageEmployeeService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}

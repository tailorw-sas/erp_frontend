package com.kynsoft.finamer.insis.infrastructure.services.kafka.consumer.manageEmployee;

import com.kynsof.share.core.domain.kafka.entity.update.UpdateManageEmployeeKafka;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.insis.application.command.manageEmployee.update.UpdateManageEmployeeCommand;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ConsumerUpdateManageEmployeeService {

    private final IMediator mediator;

    public ConsumerUpdateManageEmployeeService(IMediator mediator){
        this.mediator = mediator;
    }

    @KafkaListener(topics = "finamer-update-manage-employee", groupId = "innsist-entity-replica")
    public void listen(UpdateManageEmployeeKafka entity){
        try{
            UpdateManageEmployeeCommand command = new UpdateManageEmployeeCommand(
                    entity.getId(),
                    entity.getFirstName(),
                    entity.getLastName(),
                    entity.getEmail(),
                    entity.getManageHotelList(),
                    entity.getManageAgencyList()
            );
            mediator.send(command);
        }catch (Exception ex){
            Logger.getLogger(ConsumerUpdateManageEmployeeService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}

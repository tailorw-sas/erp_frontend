package com.tailorw.finamer.scheduler.infrastructure.services.kafka.consumer;

import com.kynsof.share.core.domain.kafka.entity.scheduler.UpdateSchedulerProcessKafka;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.tailorw.finamer.scheduler.application.command.schedulerLogProcess.updateLog.UpdateSchedulerLogProcessCommand;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ConsumerUpdateSchedulerProcessService {

    private final IMediator mediator;

    public ConsumerUpdateSchedulerProcessService(IMediator mediator){
        this.mediator = mediator;
    }

    @KafkaListener(topics = "finamer-update-scheduler-process", groupId = "scheduler-entity-update")
    public void listen(UpdateSchedulerProcessKafka entity){
        try{
            UpdateSchedulerLogProcessCommand command = new UpdateSchedulerLogProcessCommand(
                    entity.getProcessId(),
                    entity.getCompletedAt(),
                    entity.getAdditionalDetails());
            mediator.send(command);

        }catch (Exception ex){
            Logger.getLogger(ConsumerUpdateSchedulerProcessService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}

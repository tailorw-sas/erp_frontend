package com.tailorw.tcaInnsist.infrastructure.service.kafka.producer;

import com.kynsof.share.core.domain.kafka.entity.scheduler.UpdateSchedulerProcessKafka;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class ProducerUpdateSchedulerLogService {
    private final KafkaTemplate<String, Object> producer;

    public ProducerUpdateSchedulerLogService(KafkaTemplate<String, Object> producer){
        this.producer = producer;
    }

    @Async
    public void update(UpdateSchedulerProcessKafka entity){
        try{
            producer.send("finamer-update-scheduler-process", entity);
        }catch (Exception ex){

        }
    }
}

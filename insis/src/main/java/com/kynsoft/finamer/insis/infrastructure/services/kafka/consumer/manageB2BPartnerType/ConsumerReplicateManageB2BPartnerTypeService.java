package com.kynsoft.finamer.insis.infrastructure.services.kafka.consumer.manageB2BPartnerType;

import com.kynsof.share.core.domain.kafka.entity.ReplicateB2BPartnerTypeKafka;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.insis.application.command.manageB2BPartnerType.create.CreateB2BPartnerTypeCommand;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class ConsumerReplicateManageB2BPartnerTypeService {
    private final IMediator mediator;

    public ConsumerReplicateManageB2BPartnerTypeService(IMediator mediator){
        this.mediator = mediator;
    }

    @KafkaListener(topics = "finamer-replicate-b2b-partner-type", groupId = "innsist-entity-replica")
    public void listen(ReplicateB2BPartnerTypeKafka objKafka){
        if(objKafka.getCode().equals("TCA")){
            CreateB2BPartnerTypeCommand command = new CreateB2BPartnerTypeCommand(
                    objKafka.getId(),
                    objKafka.getCode(),
                    objKafka.getName(),
                    objKafka.getStatus()
            );

            mediator.send(command);
        }
    }
}

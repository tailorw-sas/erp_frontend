package com.kynsoft.finamer.insis.infrastructure.services.kafka.consumer.manageTradingCompany;

import com.kynsof.share.core.domain.kafka.entity.ReplicateManageTradingCompanyKafka;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.insis.application.command.manageTradingCompany.create.CreateTradingCompanyCommand;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ConsumerReplicateManageTradingCompanyService {
    private final IMediator mediator;

    public ConsumerReplicateManageTradingCompanyService(IMediator mediator){
        this.mediator = mediator;
    }

    @KafkaListener(topics = "finamer-replicate-manage-trading-company", groupId = "innsist-entity-replica")
    public void listen(ReplicateManageTradingCompanyKafka entity){
        try{
            CreateTradingCompanyCommand command = new CreateTradingCompanyCommand(
                    entity.getId(),
                    entity.getCode(),
                    entity.getCompany(),
                    entity.getInnsistCode(),
                    entity.getStatus()
            );
            mediator.send(command);
        }catch (Exception ex){
            Logger.getLogger(ConsumerReplicateManageTradingCompanyService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}

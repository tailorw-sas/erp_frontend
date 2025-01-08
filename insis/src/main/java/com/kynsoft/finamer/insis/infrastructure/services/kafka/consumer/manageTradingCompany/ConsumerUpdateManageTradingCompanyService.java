package com.kynsoft.finamer.insis.infrastructure.services.kafka.consumer.manageTradingCompany;

import com.kynsof.share.core.domain.kafka.entity.update.UpdateManageTradingCompanyKafka;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.insis.application.command.manageTradingCompany.update.UpdateTradingCompanyCommand;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ConsumerUpdateManageTradingCompanyService {
    private final IMediator mediator;

    public ConsumerUpdateManageTradingCompanyService(IMediator mediator){
        this.mediator = mediator;
    }

    @KafkaListener(topics = "finamer-update-manage-trading-company", groupId = "innsist-entity-replica")
    public void listen(UpdateManageTradingCompanyKafka entity){
        try{
            UpdateTradingCompanyCommand command = new UpdateTradingCompanyCommand(
                    entity.getId(),
                    entity.getCompany(),
                    entity.getInnsistCode()
            );
            mediator.send(command);
        }catch (Exception ex){
            Logger.getLogger(ConsumerUpdateManageTradingCompanyService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}

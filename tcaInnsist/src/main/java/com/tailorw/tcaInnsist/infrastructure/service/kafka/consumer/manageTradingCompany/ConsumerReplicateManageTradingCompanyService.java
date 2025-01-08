package com.tailorw.tcaInnsist.infrastructure.service.kafka.consumer.manageTradingCompany;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.tailorw.tcaInnsist.application.command.manageTradingCompany.create.CreateManageTradingCompanyCommand;
import com.tailorw.tcaInnsist.infrastructure.model.kafka.manageTradingCompany.ReplicateManageTradingCompanyKafka;
import lombok.AllArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
@AllArgsConstructor
public class ConsumerReplicateManageTradingCompanyService {
    private final IMediator mediator;

    @KafkaListener(topics = "finamer-innsist-replicate-manage-trading-company", groupId = "tcaInnsist-replicate-entity")
    public void listen(String message){
        try{
            ObjectMapper mapper = new ObjectMapper();
            ReplicateManageTradingCompanyKafka objKafka = mapper.readValue(message, new TypeReference<ReplicateManageTradingCompanyKafka>() {});
            CreateManageTradingCompanyCommand command = new CreateManageTradingCompanyCommand(
                    objKafka.getId(),
                    objKafka.getCode(),
                    objKafka.getName(),
                    objKafka.getConnectionId()
            );
            mediator.send(command);
        }catch (Exception ex){
            Logger.getLogger(ConsumerReplicateManageTradingCompanyService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}

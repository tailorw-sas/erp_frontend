package com.tailorw.tcaInnsist.infrastructure.service.kafka.consumer.manageTradingCompany;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.tailorw.tcaInnsist.application.command.manageTradingCompany.delete.DeleteManageTradingCompanyCommand;
import com.tailorw.tcaInnsist.infrastructure.model.kafka.manageTradingCompany.DeleteManageTradingCompanyKafka;
import lombok.AllArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
@AllArgsConstructor
public class ConsumerDeleteManageTradingCompanyKafka {

    private final IMediator mediator;

    @KafkaListener(topics = "finamer-innsist-delete-manage-trading-company", groupId = "tcaInnsist-delete-entity")
    public void listen(String message){
        try{
            ObjectMapper mapper = new ObjectMapper();
            DeleteManageTradingCompanyKafka objKafka = mapper.readValue(message, new TypeReference<DeleteManageTradingCompanyKafka>() {});
            DeleteManageTradingCompanyCommand command = new DeleteManageTradingCompanyCommand(objKafka.getId());
            mediator.send(command);
        }catch (Exception ex){
            Logger.getLogger(ConsumerDeleteManageTradingCompanyKafka.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}

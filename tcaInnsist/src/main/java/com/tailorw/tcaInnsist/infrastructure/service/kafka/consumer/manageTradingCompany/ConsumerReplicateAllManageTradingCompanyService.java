package com.tailorw.tcaInnsist.infrastructure.service.kafka.consumer.manageTradingCompany;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.tailorw.tcaInnsist.application.command.manageTradingCompany.create.CreateManageTradingCompanyCommand;
import com.tailorw.tcaInnsist.application.command.manageTradingCompany.createMany.CreateManyManageTradingCompanyCommand;
import com.tailorw.tcaInnsist.infrastructure.model.kafka.manageTradingCompany.ReplicateAllManageTradingCompanyKafka;
import lombok.AllArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.List;

@Service
@AllArgsConstructor
public class ConsumerReplicateAllManageTradingCompanyService {
    private final IMediator mediator;

    @KafkaListener(topics = "finamer-innsist-replicate-all-manage-connection", groupId = "tcaInnsist-replicate-all-entity")
    public void listen(String message){
        try {
            ObjectMapper mapper = new ObjectMapper();
            ReplicateAllManageTradingCompanyKafka objKafkaList = mapper.readValue(message, new TypeReference<ReplicateAllManageTradingCompanyKafka>() {});
            List<CreateManageTradingCompanyCommand> createCommands = objKafkaList.getReplicateManageTradingCompanyKafkaList()
                    .stream()
                    .map(objKafka -> {
                        return new CreateManageTradingCompanyCommand(
                                objKafka.getId(),
                                objKafka.getCode(),
                                objKafka.getName(),
                                objKafka.getConnectionId()
                        );
                    }).toList();
            CreateManyManageTradingCompanyCommand command = new CreateManyManageTradingCompanyCommand(createCommands);
            mediator.send(command);
        }catch (Exception ex){
            Logger.getLogger(ConsumerReplicateAllManageTradingCompanyService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}

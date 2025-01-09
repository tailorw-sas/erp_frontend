package com.tailorw.tcaInnsist.infrastructure.service.kafka.consumer.manageConnection;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.tailorw.tcaInnsist.application.command.manageConnection.create.CreateManageConnectionCommand;
import com.tailorw.tcaInnsist.application.command.manageConnection.createMany.CreateManyManageConnectionCommand;
import com.tailorw.tcaInnsist.infrastructure.model.kafka.manageConnection.ReplicateAllManageConnectionKafka;
import com.tailorw.tcaInnsist.infrastructure.model.kafka.manageConnection.ReplicateManageConnectionKafka;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ConsumerReplicateAllManageConnectionService {
    private final IMediator mediator;

    public ConsumerReplicateAllManageConnectionService(IMediator mediator){
        this.mediator = mediator;
    }

    @KafkaListener(topics = "finamer-innsist-replicate-all-manage-connection", groupId = "tcaInnsist-replicate-all-entity")
    public void listen(String message){
        try{
            ObjectMapper mapper = new ObjectMapper();
            ReplicateAllManageConnectionKafka replicateAllManageConnectionKafka = mapper.readValue(message, new TypeReference<ReplicateAllManageConnectionKafka>() {});

            CreateManyManageConnectionCommand command = new CreateManyManageConnectionCommand(
                    convertToListDto(replicateAllManageConnectionKafka.getConfigurationPropertiesKafkaList())
            );
            mediator.send(command);

        }catch (Exception ex){
            Logger.getLogger(ConsumerReplicateAllManageConnectionService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private List<CreateManageConnectionCommand> convertToListDto(List<ReplicateManageConnectionKafka> kafkaList){
        return kafkaList.stream()
                .map(objKafka -> {
                    return new CreateManageConnectionCommand(
                            objKafka.getId(),
                            objKafka.getServer(),
                            objKafka.getPort(),
                            objKafka.getDbName(),
                            objKafka.getUserName(),
                            objKafka.getPassword()
                    );
                }).toList();
    }
}

package com.tailorw.tcaInnsist.infrastructure.service.kafka.consumer.manageConnection;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.tailorw.tcaInnsist.application.command.manageConnection.create.CreateManageConnectionCommand;
import com.tailorw.tcaInnsist.infrastructure.model.kafka.manageConnection.ReplicateManageConnectionKafka;
import lombok.AllArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
@AllArgsConstructor
public class ConsumerReplicateManageConnectionService {
    private final IMediator mediator;

    @KafkaListener(topics = "finamer-innsist-replicate-manage-connection", groupId = "TcaInnsist-replicate-entity")
    public void listen(String message){
        try{
            ObjectMapper mapper = new ObjectMapper();
            ReplicateManageConnectionKafka objKafka =  mapper.readValue(message, new TypeReference<ReplicateManageConnectionKafka>(){});
            CreateManageConnectionCommand command = new CreateManageConnectionCommand(
                    objKafka.getId(),
                    objKafka.getServer(),
                    objKafka.getPort(),
                    objKafka.getDbName(),
                    objKafka.getUserName(),
                    objKafka.getPassword()
            );
            mediator.send(command);

        }catch (Exception ex){
            Logger.getLogger(ConsumerReplicateManageConnectionService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}

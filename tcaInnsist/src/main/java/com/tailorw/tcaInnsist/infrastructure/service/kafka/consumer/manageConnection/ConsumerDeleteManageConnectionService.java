package com.tailorw.tcaInnsist.infrastructure.service.kafka.consumer.manageConnection;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.tailorw.tcaInnsist.application.command.manageConnection.delete.DeleteManageConnectionCommand;
import com.tailorw.tcaInnsist.infrastructure.model.kafka.manageConnection.DeleteManageConnectionKafka;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ConsumerDeleteManageConnectionService {

    private final IMediator mediator;

    public ConsumerDeleteManageConnectionService(IMediator mediator){
        this.mediator = mediator;
    }

    @KafkaListener(topics = "finamer-innsist-delete-manage-connection", groupId = "tcaInnsist-delete-entity")
    public void listen(String message){
        try{
            ObjectMapper mapper = new ObjectMapper();
            DeleteManageConnectionKafka objKafka = mapper.readValue(message, new TypeReference<DeleteManageConnectionKafka>(){});
            DeleteManageConnectionCommand command = new DeleteManageConnectionCommand(objKafka.getId());
            mediator.send(command);
        }catch (Exception ex){
            Logger.getLogger(ConsumerDeleteManageConnectionService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}

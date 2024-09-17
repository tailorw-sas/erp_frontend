package com.kynsoft.finamer.invoicing.infrastructure.services.kafka.consumer.manageAgency;

import com.kynsof.share.core.domain.kafka.entity.ReplicateManageAgencyKafka;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.invoicing.application.command.manageAgency.create.CreateManageAgencyCommand;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.EGenerationType;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ConsumerReplicateManageAgencyService {

    private final IMediator mediator;

    public ConsumerReplicateManageAgencyService(IMediator mediator) {

        this.mediator = mediator;
    }

    @KafkaListener(topics = "finamer-replicate-manage-agency", groupId = "invoicing-entity-replica")
    public void listen(ReplicateManageAgencyKafka objKafka) {
        try {
//            ObjectMapper objectMapper = new ObjectMapper();
//            JsonNode rootNode = objectMapper.readTree(event);
//
//            ReplicateManageAgencyKafka objKafka = objectMapper.treeToValue(rootNode, ReplicateManageAgencyKafka.class);
            CreateManageAgencyCommand command = new CreateManageAgencyCommand(objKafka.getId(), objKafka.getCode(),
                    objKafka.getName(), objKafka.getClient(), EGenerationType.valueOf(objKafka.getGenerationType()),
                    objKafka.getStatus(),objKafka.getCif(),objKafka.getAddress(),objKafka.getSentB2BPartner(),objKafka.getCityState(),objKafka.getCountry(), 
                    objKafka.getMailingAddress(), objKafka.getZipCode(), objKafka.getCity(), objKafka.getCreditDay(), objKafka.getAutoReconcile());
            mediator.send(command);
        } catch (Exception ex) {
            Logger.getLogger(ConsumerReplicateManageAgencyService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}

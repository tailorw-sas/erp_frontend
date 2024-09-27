package com.kynsoft.finamer.invoicing.infrastructure.services.kafka.consumer.manageB2BPartner;

import com.kynsof.share.core.domain.kafka.entity.ReplicateB2BPartnerKafka;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.invoicing.application.command.manageB2BPartner.create.CreateManagerB2BPartnerCommand;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.Status;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ConsumerReplicateB2BPartnerService {

    private final IMediator mediator;

    public ConsumerReplicateB2BPartnerService(IMediator mediator) {

        this.mediator = mediator;
    }

    @KafkaListener(topics = "finamer-replicate-b2b-partner", groupId = "invoicing-entity-replica")
    public void listen(ReplicateB2BPartnerKafka objKafka) {
        try {
            CreateManagerB2BPartnerCommand command1 = new CreateManagerB2BPartnerCommand(
                    objKafka.getId(),
                    objKafka.getCode(),
                    objKafka.getDescription(),
                    objKafka.getName(),
                    Status.valueOf(objKafka.getStatus()),
                    objKafka.getUrl(),
                    objKafka.getIp(),
                    objKafka.getUserName(),
                    objKafka.getPassword(),
                    objKafka.getToken(),
                    objKafka.getB2BPartnerTypeDto()
            );
            mediator.send(command1);
        } catch (Exception ex) {
            Logger.getLogger(ConsumerReplicateB2BPartnerService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}

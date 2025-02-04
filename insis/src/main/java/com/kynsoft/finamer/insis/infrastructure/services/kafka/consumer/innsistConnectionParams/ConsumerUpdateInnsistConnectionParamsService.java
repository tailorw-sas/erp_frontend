package com.kynsoft.finamer.insis.infrastructure.services.kafka.consumer.innsistConnectionParams;

import com.kynsof.share.core.domain.kafka.entity.update.UpdateManageB2BPartnerKafka;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.insis.application.command.innsistConnectionParams.update.UpdateConnectionParamsCommand;
import com.kynsoft.finamer.insis.application.query.manageB2BPartnerType.getById.FindB2BPartnerTypeByIdQuery;
import com.kynsoft.finamer.insis.application.query.objectResponse.manageB2BPartnerType.ManageB2BPartnerTypeResponse;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ConsumerUpdateInnsistConnectionParamsService {
    private final IMediator mediator;

    public ConsumerUpdateInnsistConnectionParamsService(IMediator mediator){
        this.mediator = mediator;
    }

    @KafkaListener(topics = "finamer-update-b2b-partner", groupId = "innsist-entity-replica")
    public void listen(UpdateManageB2BPartnerKafka entity){
        try{
            UUID partnerTypeId = entity.getB2BPartnerTypeDto();
            if(validateIfB2BPartnerTypeIsInnsistConnectionParameter(partnerTypeId)) {
                UpdateConnectionParamsCommand command = new UpdateConnectionParamsCommand(
                        entity.getId(),
                        "",
                        0,
                        entity.getName(),
                        "",
                        "",
                        entity.getDescription(),
                        entity.getStatus()
                );
                mediator.send(command);
            }

        }catch (Exception ex){
            Logger.getLogger(ConsumerUpdateInnsistConnectionParamsService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private boolean validateIfB2BPartnerTypeIsInnsistConnectionParameter(UUID id){
        FindB2BPartnerTypeByIdQuery query = new FindB2BPartnerTypeByIdQuery(id);
        ManageB2BPartnerTypeResponse response = mediator.send(query);

        return Objects.nonNull(response) ? true : false;
    }
}

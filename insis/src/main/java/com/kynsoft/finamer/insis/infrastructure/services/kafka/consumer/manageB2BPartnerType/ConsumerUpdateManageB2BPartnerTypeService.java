package com.kynsoft.finamer.insis.infrastructure.services.kafka.consumer.manageB2BPartnerType;

import com.kynsof.share.core.domain.kafka.entity.update.UpdateManageB2BPartnerTypeKafka;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.insis.application.command.manageB2BPartnerType.update.UpdateB2BPartnerTypeCommand;
import com.kynsoft.finamer.insis.application.query.manageB2BPartnerType.getById.FindB2BPartnerTypeByIdQuery;
import com.kynsoft.finamer.insis.application.query.objectResponse.manageB2BPartnerType.ManageB2BPartnerTypeResponse;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.UUID;

@Service
public class ConsumerUpdateManageB2BPartnerTypeService {
    private final IMediator mediator;

    public ConsumerUpdateManageB2BPartnerTypeService(IMediator mediator){
        this.mediator = mediator;
    }

    @KafkaListener(topics = "finamer-update-b2b-partner-type", groupId = "innsist-entity-replica")
    public void listen(UpdateManageB2BPartnerTypeKafka objKafka){
        if(validateIfB2BPartnerTypeIsInnsistConnectionParameter(objKafka.getId())){
            UpdateB2BPartnerTypeCommand command = new UpdateB2BPartnerTypeCommand(
                    objKafka.getId(),
                    objKafka.getCode(),
                    objKafka.getName(),
                    objKafka.getStatus()
            );

            mediator.send(command);
        }
    }

    private boolean validateIfB2BPartnerTypeIsInnsistConnectionParameter(UUID id){
        FindB2BPartnerTypeByIdQuery query = new FindB2BPartnerTypeByIdQuery(id);
        ManageB2BPartnerTypeResponse response = mediator.send(query);

        return Objects.nonNull(response) ? true : false;
    }
}

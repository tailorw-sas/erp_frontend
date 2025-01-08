package com.kynsoft.finamer.insis.infrastructure.services.kafka.consumer.innsistConnectionParams;

import com.kynsof.share.core.domain.kafka.entity.ReplicateB2BPartnerKafka;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.insis.application.command.innsistConnectionParams.create.CreateConnectionParamsCommand;
import com.kynsoft.finamer.insis.application.command.innsistConnectionParams.update.UpdateConnectionParamsCommand;
import com.kynsoft.finamer.insis.application.query.innsistConnectionParamsQuery.getById.FindInnsistConnectionParamsByIdQuery;
import com.kynsoft.finamer.insis.application.query.manageB2BPartnerType.getById.FindB2BPartnerTypeByIdQuery;
import com.kynsoft.finamer.insis.application.query.objectResponse.manageB2BPartnerType.ManageB2BPartnerTypeResponse;
import com.kynsoft.finamer.insis.infrastructure.services.kafka.consumer.manageB2BPartnerType.ConsumerReplicateManageB2BPartnerTypeService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ConsumerReplicateInnsistConnectionParamsService {
    private final IMediator mediator;

    public ConsumerReplicateInnsistConnectionParamsService(IMediator mediator){
        this.mediator = mediator;
    }

    @KafkaListener(topics = "finamer-replicate-b2b-partner", groupId = "innsist-entity-replica")
    public void listen(ReplicateB2BPartnerKafka objKafka){
        try {
            UUID partnerTypeId = objKafka.getB2BPartnerTypeDto();
            if(validateIfB2BPartnerTypeIsInnsistConnectionParameter(partnerTypeId)){
                if(!validateIfB2BPartnerIsInnsistConnectionParameter(objKafka.getId())){
                    CreateConnectionParamsCommand command = new CreateConnectionParamsCommand(
                            objKafka.getId(),
                            objKafka.getIp(),
                            isNumeric(objKafka.getToken()) ? Integer.parseInt(objKafka.getToken()) : 0,
                            objKafka.getName(),
                            objKafka.getUserName(),
                            objKafka.getPassword(),
                            objKafka.getDescription(),
                            objKafka.getStatus()
                    );
                    mediator.send(command);
                }
                else{
                    UpdateConnectionParamsCommand command = new UpdateConnectionParamsCommand(
                            objKafka.getId(),
                            objKafka.getIp(),
                            isNumeric(objKafka.getToken()) ? Integer.parseInt(objKafka.getToken()) : 0,
                            objKafka.getName(),
                            objKafka.getUserName(),
                            objKafka.getPassword(),
                            objKafka.getDescription(),
                            objKafka.getStatus()
                    );
                    mediator.send(command);
                }
            }

        } catch (Exception ex) {
            Logger.getLogger(ConsumerReplicateManageB2BPartnerTypeService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static boolean isNumeric(String input){
        return input.matches("^\\d+$");
    }

    private boolean validateIfB2BPartnerTypeIsInnsistConnectionParameter(UUID id){
        FindB2BPartnerTypeByIdQuery query = new FindB2BPartnerTypeByIdQuery(id);
        ManageB2BPartnerTypeResponse response = mediator.send(query);

        return Objects.nonNull(response) ? true : false;
    }

    private boolean validateIfB2BPartnerIsInnsistConnectionParameter(UUID id){
        FindInnsistConnectionParamsByIdQuery query = new FindInnsistConnectionParamsByIdQuery(id);
        return Objects.nonNull(mediator.send(query)) ? true : false;
    }
}

package com.kynsoft.finamer.creditcard.infrastructure.services.kafka.consumer.manageMerchant;

import com.kynsof.share.core.domain.kafka.entity.update.UpdateManageMerchantKafka;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.creditcard.application.command.manageMerchant.update.UpdateManagerMerchantCommand;
import com.kynsoft.finamer.creditcard.domain.dtoEnum.Status;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ConsumerUpdateManagerMerchantService {
    private final IMediator mediator;

    public ConsumerUpdateManagerMerchantService(IMediator mediator) {
        this.mediator = mediator;
    }

    @KafkaListener(topics = "finamer-update-manage-merchant", groupId = "vcc-entity-replica")
    public void listen(UpdateManageMerchantKafka objKafka) {
        try {
            UpdateManagerMerchantCommand command = new UpdateManagerMerchantCommand(objKafka.getId(), objKafka.getCode(), objKafka.getDescription(), objKafka.getB2bPartner(), objKafka.getDefaultm(), Status.valueOf(objKafka.getStatus()));
            mediator.send(command);
        } catch (Exception ex) {
            Logger.getLogger(ConsumerUpdateManagerMerchantService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}

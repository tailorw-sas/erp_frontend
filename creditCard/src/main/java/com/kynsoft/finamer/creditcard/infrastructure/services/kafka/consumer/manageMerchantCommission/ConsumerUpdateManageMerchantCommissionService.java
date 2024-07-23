package com.kynsoft.finamer.creditcard.infrastructure.services.kafka.consumer.manageMerchantCommission;

import com.kynsof.share.core.domain.kafka.entity.update.UpdateManageMerchantCommissionKafka;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.creditcard.application.command.manageMerchantCommission.update.UpdateManageMerchantCommissionCommand;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ConsumerUpdateManageMerchantCommissionService {

    private final IMediator mediator;

    public ConsumerUpdateManageMerchantCommissionService(IMediator mediator) {
        this.mediator = mediator;
    }

    @KafkaListener(topics = "finamer-update-manage-merchant-commission", groupId = "vcc-entity-replica")
    public void listen(UpdateManageMerchantCommissionKafka objKafka) {
        try {
            UpdateManageMerchantCommissionCommand command = new UpdateManageMerchantCommissionCommand(
                    objKafka.getId(),
                    objKafka.getManagerMerchant(),
                    objKafka.getManageCreditCartType(),
                    objKafka.getCommission(),
                    objKafka.getCalculationType(),
                    objKafka.getFromDate(),
                    objKafka.getToDate(),
                    objKafka.getStatus());
            mediator.send(command);
        } catch (Exception ex) {
            Logger.getLogger(ConsumerUpdateManageMerchantCommissionService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}

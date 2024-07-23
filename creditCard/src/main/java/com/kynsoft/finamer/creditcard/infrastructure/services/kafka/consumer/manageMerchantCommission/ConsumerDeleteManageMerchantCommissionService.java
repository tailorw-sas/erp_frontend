package com.kynsoft.finamer.creditcard.infrastructure.services.kafka.consumer.manageMerchantCommission;

import com.kynsof.share.core.domain.kafka.entity.vcc.DeleteManageMerchantCommissionKafka;
import com.kynsof.share.core.domain.kafka.entity.vcc.DeleteManageMerchantHotelEnrolleKafka;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.creditcard.application.command.manageMerchantCommission.delete.DeleteManageMerchantCommissionCommand;
import com.kynsoft.finamer.creditcard.application.command.manageMerchantHotelEnrolle.delete.DeleteManagerMerchantHotelEnrolleCommand;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ConsumerDeleteManageMerchantCommissionService {

    private final IMediator mediator;

    public ConsumerDeleteManageMerchantCommissionService(IMediator mediator) {
        this.mediator = mediator;
    }

    @KafkaListener(topics = "finamer-delete-manage-merchant-commission", groupId = "vcc-entity-replica")
    public void listen(DeleteManageMerchantCommissionKafka entity) {
        try {
            DeleteManageMerchantCommissionCommand command = new DeleteManageMerchantCommissionCommand(entity.getId());
            mediator.send(command);
        } catch (Exception ex) {
            Logger.getLogger(ConsumerDeleteManageMerchantCommissionService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}

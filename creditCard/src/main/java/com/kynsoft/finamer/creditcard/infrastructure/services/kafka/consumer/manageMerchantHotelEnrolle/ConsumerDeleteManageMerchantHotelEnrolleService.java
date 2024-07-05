package com.kynsoft.finamer.creditcard.infrastructure.services.kafka.consumer.manageMerchantHotelEnrolle;

import com.kynsof.share.core.domain.kafka.entity.vcc.DeleteManageMerchantHotelEnrolleKafka;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.creditcard.application.command.manageMerchantHotelEnrolle.delete.DeleteManagerMerchantHotelEnrolleCommand;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ConsumerDeleteManageMerchantHotelEnrolleService {

    private final IMediator mediator;

    public ConsumerDeleteManageMerchantHotelEnrolleService(IMediator mediator) {
        this.mediator = mediator;
    }

    @KafkaListener(topics = "finamer-delete-manage-merchant-hotel-enrolle", groupId = "vcc-entity-replica")
    public void listen(DeleteManageMerchantHotelEnrolleKafka entity) {
        try {
            DeleteManagerMerchantHotelEnrolleCommand command = new DeleteManagerMerchantHotelEnrolleCommand(entity.getId());
            mediator.send(command);
        } catch (Exception ex) {
            Logger.getLogger(ConsumerDeleteManageMerchantHotelEnrolleService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}

package com.kynsoft.finamer.creditcard.infrastructure.services.kafka.consumer.manageMerchantHotelEnrolle;

import com.kynsof.share.core.domain.kafka.entity.update.UpdateManageMerchantHotelEnrolleKafka;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.creditcard.application.command.manageMerchantHotelEnrolle.update.UpdateManagerMerchantHotelEnrolleCommand;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ConsumerUpdateManageMerchantHotelEnrolleService {

    private final IMediator mediator;

    public ConsumerUpdateManageMerchantHotelEnrolleService(IMediator mediator) {
        this.mediator = mediator;
    }

    @KafkaListener(topics = "finamer-update-manage-merchant-hotel-enrolle", groupId = "vcc-entity-replica")
    public void listen(UpdateManageMerchantHotelEnrolleKafka objKafka) {
        try {
            UpdateManagerMerchantHotelEnrolleCommand command = new UpdateManagerMerchantHotelEnrolleCommand(objKafka.getId(), objKafka.getManagerMerchant(), objKafka.getManagerHotel(), objKafka.getEnrrolle(), objKafka.getStatus());
            mediator.send(command);
        } catch (Exception ex) {
            Logger.getLogger(ConsumerUpdateManageMerchantHotelEnrolleService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}

package com.kynsoft.finamer.creditcard.infrastructure.services.kafka.consumer.manageMerchantHotelEnrolle;

import com.kynsof.share.core.domain.kafka.entity.vcc.ReplicateManageMerchantHotelEnrolleKafka;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.creditcard.application.command.manageMerchantHotelEnrolle.create.CreateManageMerchantHotelEnrolleCommand;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ConsumerReplicateManageMerchantHotelEnrolleService {

    private final IMediator mediator;

    public ConsumerReplicateManageMerchantHotelEnrolleService(IMediator mediator) {
        this.mediator = mediator;
    }

    @KafkaListener(topics = "finamer-replicate-manage-merchant-hotel-enrolle", groupId = "vcc-entity-replica")
    public void listen(ReplicateManageMerchantHotelEnrolleKafka entity) {
        try {
            CreateManageMerchantHotelEnrolleCommand command = new CreateManageMerchantHotelEnrolleCommand(entity.getId(), entity.getManagerMerchant(), entity.getManagerHotel(), entity.getEnrrolle());
            mediator.send(command);
        } catch (Exception ex) {
            Logger.getLogger(ConsumerReplicateManageMerchantHotelEnrolleService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}

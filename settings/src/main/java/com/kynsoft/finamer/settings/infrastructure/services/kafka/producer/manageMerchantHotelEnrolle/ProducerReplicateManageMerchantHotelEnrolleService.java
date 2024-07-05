package com.kynsoft.finamer.settings.infrastructure.services.kafka.producer.manageMerchantHotelEnrolle;

import com.kynsof.share.core.domain.kafka.entity.vcc.ReplicateManageMerchantHotelEnrolleKafka;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ProducerReplicateManageMerchantHotelEnrolleService {

    private final KafkaTemplate<String, Object> producer;

    public ProducerReplicateManageMerchantHotelEnrolleService(KafkaTemplate<String, Object> producer) {
        this.producer = producer;
    }

    @Async
    public void create(ReplicateManageMerchantHotelEnrolleKafka entity) {

        try {
            this.producer.send("finamer-replicate-manage-merchant-hotel-enrolle", entity);
        } catch (Exception ex) {
            Logger.getLogger(ProducerReplicateManageMerchantHotelEnrolleService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
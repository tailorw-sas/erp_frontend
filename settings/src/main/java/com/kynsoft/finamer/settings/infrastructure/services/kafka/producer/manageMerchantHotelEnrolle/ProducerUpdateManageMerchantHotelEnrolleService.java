package com.kynsoft.finamer.settings.infrastructure.services.kafka.producer.manageMerchantHotelEnrolle;

import com.kynsof.share.core.domain.kafka.entity.update.UpdateManageMerchantHotelEnrolleKafka;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ProducerUpdateManageMerchantHotelEnrolleService {

    private final KafkaTemplate<String, Object> producer;

    public ProducerUpdateManageMerchantHotelEnrolleService(KafkaTemplate<String, Object> producer) {
        this.producer = producer;
    }

    @Async
    public void update(UpdateManageMerchantHotelEnrolleKafka entity) {

        try {
            this.producer.send("finamer-update-manage-merchant-hotel-enrolle", entity);
        } catch (Exception ex) {
            Logger.getLogger(ProducerUpdateManageMerchantHotelEnrolleService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
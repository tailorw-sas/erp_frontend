package com.kynsoft.finamer.settings.infrastructure.services.kafka.producer.manageMerchantHotelEnrolle;

import com.kynsof.share.core.domain.kafka.entity.vcc.DeleteManageMerchantHotelEnrolleKafka;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ProducerDeleteManageMerchantHotelEnrolleService {

    private final KafkaTemplate<String, Object> producer;

    public ProducerDeleteManageMerchantHotelEnrolleService(KafkaTemplate<String, Object> producer) {
        this.producer = producer;
    }

    @Async
    public void delete(DeleteManageMerchantHotelEnrolleKafka entity) {

        try {
            this.producer.send("finamer-delete-manage-merchant-hotel-enrolle", entity);
        } catch (Exception ex) {
            Logger.getLogger(ProducerDeleteManageMerchantHotelEnrolleService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}

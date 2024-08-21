package com.kynsoft.finamer.payment.infrastructure.services.kafka.producer.updateBooking;

import com.kynsof.share.core.domain.kafka.entity.update.UpdateBookingBalanceKafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ProducerUpdateBookingService {
    private final KafkaTemplate<String, Object> producer;

    public ProducerUpdateBookingService(KafkaTemplate<String, Object> producer) {
        this.producer = producer;
    }

    @Async
    public void update(UpdateBookingBalanceKafka entity) {

        try {
            this.producer.send("finamer-update-booking-balance", entity);
        } catch (Exception ex) {
            Logger.getLogger(ProducerUpdateBookingService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
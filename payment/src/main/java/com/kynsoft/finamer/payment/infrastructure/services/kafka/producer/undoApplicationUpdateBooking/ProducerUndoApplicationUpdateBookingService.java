package com.kynsoft.finamer.payment.infrastructure.services.kafka.producer.undoApplicationUpdateBooking;

import com.kynsof.share.core.domain.kafka.entity.update.UpdateBookingBalanceKafka;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ProducerUndoApplicationUpdateBookingService {
    private final KafkaTemplate<String, Object> producer;

    public ProducerUndoApplicationUpdateBookingService(KafkaTemplate<String, Object> producer) {
        this.producer = producer;
    }

    @Async
    public void update(UpdateBookingBalanceKafka entity) {

        try {
            this.producer.send("finamer-undo-application-update-booking-balance", entity);
        } catch (Exception ex) {
            Logger.getLogger(ProducerUndoApplicationUpdateBookingService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
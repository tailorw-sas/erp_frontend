package com.kynsoft.finamer.payment.infrastructure.services.kafka.producer.updateBooking;

import com.kynsof.share.core.domain.kafka.entity.update.UpdateBookingBalanceKafka;
import java.util.List;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ProducerUpdateListBookingService {
    private final KafkaTemplate<String, Object> producer;

    public ProducerUpdateListBookingService(KafkaTemplate<String, Object> producer) {
        this.producer = producer;
    }

    @Async
    public void update(List<UpdateBookingBalanceKafka> kafkaList) {

        try {
            for (UpdateBookingBalanceKafka entity : kafkaList) {
                this.producer.send("finamer-update-booking-balance", entity);
                System.err.println("##############################################");
                System.err.println("##############################################");
                System.err.println("Se envia a kafka");
                System.err.println("##############################################");
                System.err.println("##############################################");
            }
        } catch (Exception ex) {
            Logger.getLogger(ProducerUpdateListBookingService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
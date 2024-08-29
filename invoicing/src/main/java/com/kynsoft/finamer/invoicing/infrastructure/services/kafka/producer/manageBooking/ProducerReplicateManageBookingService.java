package com.kynsoft.finamer.invoicing.infrastructure.services.kafka.producer.manageBooking;

import com.kynsof.share.core.domain.kafka.entity.ManageBookingKafka;
import com.kynsoft.finamer.invoicing.domain.dto.ManageBookingDto;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ProducerReplicateManageBookingService {

    private final KafkaTemplate<String, Object> producer;

    public ProducerReplicateManageBookingService(KafkaTemplate<String, Object> producer) {
        this.producer = producer;
    }

    @Async
    public void create(ManageBookingDto entity){
        try {
            this.producer.send("finamer-replicate-manage-booking", new ManageBookingKafka(
                    entity.getId(), 
                    entity.getBookingId(),
                    entity.getHotelBookingNumber(), 
                    entity.getCheckIn(), 
                    entity.getCheckOut(), 
                    entity.getFullName(), 
                    entity.getFirstName(), 
                    entity.getLastName(), 
                    entity.getInvoiceAmount(), 
                    entity.getDueAmount(), 
                    entity.getCouponNumber(), 
                    entity.getAdults(), 
                    entity.getChildren(), 
                    entity.getInvoice() != null ? entity.getInvoice().getId() : null
            ));
        } catch (Exception ex) {
            Logger.getLogger(ProducerReplicateManageBookingService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}

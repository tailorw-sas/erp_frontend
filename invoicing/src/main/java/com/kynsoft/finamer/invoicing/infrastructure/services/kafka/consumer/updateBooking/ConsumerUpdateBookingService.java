package com.kynsoft.finamer.invoicing.infrastructure.services.kafka.consumer.updateBooking;

import com.kynsof.share.core.domain.kafka.entity.update.UpdateBookingBalanceKafka;
import com.kynsoft.finamer.invoicing.domain.dto.ManageBookingDto;
import com.kynsoft.finamer.invoicing.domain.services.IManageBookingService;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ConsumerUpdateBookingService {

    private final IManageBookingService bookingService;

    public ConsumerUpdateBookingService(IManageBookingService bookingService) {
        this.bookingService = bookingService;
    }

    @KafkaListener(topics = "finamer-update-booking-balance", groupId = "invoicing-entity-replica")
    public void listen(UpdateBookingBalanceKafka objKafka) {
        try {

            ManageBookingDto bookingDto = this.bookingService.findById(objKafka.getId());
            bookingDto.setDueAmount(objKafka.getAmountBalance());
            this.bookingService.update(bookingDto);
        } catch (Exception ex) {
            Logger.getLogger(ConsumerUpdateBookingService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}

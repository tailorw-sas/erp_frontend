package com.kynsoft.finamer.payment.infrastructure.services.kafka.consumer.manageInvoice;

import com.kynsof.share.core.domain.kafka.entity.ManageBookingKafka;
import com.kynsof.share.core.domain.kafka.entity.ManageInvoiceKafka;
import com.kynsoft.finamer.payment.domain.dto.ManageBookingDto;
import com.kynsoft.finamer.payment.domain.dto.ManageInvoiceDto;
import com.kynsoft.finamer.payment.domain.services.IManageInvoiceService;
import java.util.ArrayList;
import java.util.List;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ConsumerReplicateManageInvoiceService {

    private final IManageInvoiceService service;

    public ConsumerReplicateManageInvoiceService(IManageInvoiceService service) {

        this.service = service;
    }

    @KafkaListener(topics = "finamer-replicate-manage-invoice", groupId = "payment-entity-replica")
    public void listen(ManageInvoiceKafka objKafka) {
        try {
            List<ManageBookingDto> bookingDtos = new ArrayList<>();
            if (objKafka.getBookings() != null) {
                for (ManageBookingKafka booking : objKafka.getBookings()) {
                    bookingDtos.add(new ManageBookingDto(
                            booking.getId(), 
                            booking.getBookingId(),
                            booking.getReservationNumber(), 
                            booking.getCheckIn(), 
                            booking.getCheckOut(), 
                            booking.getFullName(), 
                            booking.getFirstName(), 
                            booking.getLastName(), 
                            booking.getInvoiceAmount(), 
                            booking.getAmountBalance(), 
                            booking.getCouponNumber(), 
                            booking.getAdults(), 
                            booking.getChildren(), 
                            null
                    ));
                }
            }
            this.service.create(new ManageInvoiceDto(
                    objKafka.getId(), 
                    objKafka.getInvoiceId(), 
                    objKafka.getInvoiceNo(), 
                    objKafka.getInvoiceNumber(), 
                    objKafka.getInvoiceAmount(), 
                    bookingDtos
            ));
        } catch (Exception ex) {
            Logger.getLogger(ConsumerReplicateManageInvoiceService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}

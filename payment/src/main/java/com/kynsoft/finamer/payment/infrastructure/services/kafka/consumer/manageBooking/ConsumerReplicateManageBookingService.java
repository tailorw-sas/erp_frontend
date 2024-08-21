package com.kynsoft.finamer.payment.infrastructure.services.kafka.consumer.manageBooking;

import com.kynsof.share.core.domain.kafka.entity.ManageBookingKafka;
import com.kynsoft.finamer.payment.domain.dto.ManageBookingDto;
import com.kynsoft.finamer.payment.domain.dto.ManageInvoiceDto;
import com.kynsoft.finamer.payment.domain.services.IManageBookingService;
import com.kynsoft.finamer.payment.domain.services.IManageInvoiceService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ConsumerReplicateManageBookingService {

    private final IManageInvoiceService service;
    private final IManageBookingService serviceBookingService;

    public ConsumerReplicateManageBookingService(IManageInvoiceService service,
                                                 IManageBookingService serviceBookingService) {
        this.service = service;
        this.serviceBookingService = serviceBookingService;
    }

    @KafkaListener(topics = "finamer-replicate-manage-booking", groupId = "payment-entity-replica")
    public void listen(ManageBookingKafka objKafka) {
        try {
            ManageInvoiceDto invoiceDto = objKafka.getInvoice() != null ? this.service.findById(objKafka.getInvoice()) : null;
            this.serviceBookingService.create(new ManageBookingDto(
                    objKafka.getId(), 
                    objKafka.getBookingId(),
                    objKafka.getReservationNumber(), 
                    objKafka.getCheckIn(), 
                    objKafka.getCheckOut(), 
                    objKafka.getFullName(), 
                    objKafka.getFirstName(), 
                    objKafka.getLastName(), 
                    objKafka.getInvoiceAmount(), 
                    objKafka.getAmountBalance(), 
                    objKafka.getCouponNumber(), 
                    objKafka.getAdults(), 
                    objKafka.getChildren(), 
                    invoiceDto
            ));
        } catch (Exception ex) {
            Logger.getLogger(ConsumerReplicateManageBookingService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}

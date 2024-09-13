package com.kynsoft.finamer.invoicing.infrastructure.services.kafka.producer.manageInvoice;

import com.kynsof.share.core.domain.kafka.entity.AttachmentKafka;
import com.kynsof.share.core.domain.kafka.entity.ManageBookingKafka;
import com.kynsof.share.core.domain.kafka.entity.ManageInvoiceKafka;
import com.kynsoft.finamer.invoicing.domain.dto.ManageAttachmentDto;
import com.kynsoft.finamer.invoicing.domain.dto.ManageBookingDto;
import com.kynsoft.finamer.invoicing.domain.dto.ManageInvoiceDto;
import com.kynsoft.finamer.invoicing.domain.services.IManageBookingService;
import java.util.ArrayList;
import java.util.List;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ProducerReplicateManageInvoiceService {

    private final KafkaTemplate<String, Object> producer;
    private final IManageBookingService manageBookingService;

    public ProducerReplicateManageInvoiceService(KafkaTemplate<String, Object> producer,
                                                IManageBookingService manageBookingService) {
        this.producer = producer;
        this.manageBookingService = manageBookingService;
    }

    @Async
    public void create(ManageInvoiceDto entity) {
        try {
            List<ManageBookingKafka> bookingKafkas = new ArrayList<>();
            if (entity.getBookings() != null) {
                for (ManageBookingDto booking : entity.getBookings()) {
                    ManageBookingDto bookingDto = this.manageBookingService.findById(booking.getId());
                    bookingKafkas.add(new ManageBookingKafka(
                            booking.getId(),
                            booking.getBookingId(),
                            booking.getHotelBookingNumber(),
                            booking.getCheckIn(),
                            booking.getCheckOut(),
                            booking.getFullName(),
                            booking.getFirstName(),
                            booking.getLastName(),
                            booking.getInvoiceAmount(),
                            booking.getDueAmount(),
                            booking.getCouponNumber(),
                            booking.getAdults(),
                            booking.getChildren(),
                            entity.getId(),
                            bookingDto.getParent() != null ? bookingDto.getParent().getId() : null
                    ));
                }
            }

            List<AttachmentKafka> attachmentKafkas = new ArrayList<>();
            if (entity.getAttachments() != null) {
                for (ManageAttachmentDto attDto : entity.getAttachments()) {
                    attachmentKafkas.add(new AttachmentKafka(
                            attDto.getId(), 
                            attDto.getEmployeeId(), 
                            attDto.getFilename(), 
                            "", 
                            attDto.getFile(), 
                            attDto.getRemark()
                    ));
                }
            }

            this.producer.send("finamer-replicate-manage-invoice", new ManageInvoiceKafka(
                    entity.getId(),
                    entity.getHotel().getId(),
                    entity.getAgency().getClient().getId(),
                    entity.getParent() != null ? entity.getParent().getId() : null,
                    entity.getAgency().getId(),
                    entity.getInvoiceId(),
                    entity.getInvoiceNo(),
                    entity.getInvoiceNumber(),
                    entity.getInvoiceType().toString(),
                    entity.getInvoiceAmount(),
                    bookingKafkas,
                    attachmentKafkas,
                    !entity.getAttachments().isEmpty()
            ));
        } catch (Exception ex) {
            Logger.getLogger(ProducerReplicateManageInvoiceService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}

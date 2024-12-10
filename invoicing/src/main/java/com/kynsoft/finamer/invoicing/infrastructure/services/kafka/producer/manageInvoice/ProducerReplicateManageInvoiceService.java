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
import java.util.UUID;
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
    public void create(ManageInvoiceDto entity, UUID attachmentDefault) {
        try {
            List<ManageBookingKafka> bookingKafkas = new ArrayList<>();
            if (entity.getBookings() != null) {
                for (ManageBookingDto booking : entity.getBookings()) {
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
                            booking.getParent() != null ? booking.getParent().getId() : null,
                            booking.getBookingDate()
                    ));
                }
            }

            List<AttachmentKafka> attachmentKafkas = new ArrayList<>();
            if (entity.getAttachments() != null) {
                boolean attDefaults = false;
                for (ManageAttachmentDto attDto : entity.getAttachments()) {

                    if(attDto.getId().equals(attachmentDefault))
                        attDefaults = true;

                    attachmentKafkas.add(new AttachmentKafka(
                            attDto.getId(), 
                            attDto.getEmployeeId(), 
                            attDto.getFilename(), 
                            "", 
                            attDto.getFile(), 
                            attDto.getRemark(),
                            attDefaults
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
                    entity.getAttachments() != null && !entity.getAttachments().isEmpty(),
                    entity.getInvoiceDate(),
                    entity.getAutoRec()
            ));
        } catch (Exception ex) {
            Logger.getLogger(ProducerReplicateManageInvoiceService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}

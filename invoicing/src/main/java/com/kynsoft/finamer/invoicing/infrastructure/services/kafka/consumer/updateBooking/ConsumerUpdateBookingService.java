package com.kynsoft.finamer.invoicing.infrastructure.services.kafka.consumer.updateBooking;

import com.kynsof.share.core.domain.kafka.entity.update.UpdateBookingBalanceKafka;
import com.kynsoft.finamer.invoicing.domain.dto.ManageBookingDto;
import com.kynsoft.finamer.invoicing.domain.dto.ManageInvoiceDto;
import com.kynsoft.finamer.invoicing.domain.services.IManageBookingService;
import com.kynsoft.finamer.invoicing.domain.services.IManageInvoiceService;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ConsumerUpdateBookingService {

    private final IManageBookingService bookingService;
    private final IManageInvoiceService invoiceService;

    public ConsumerUpdateBookingService(IManageBookingService bookingService, IManageInvoiceService invoiceService) {
        this.bookingService = bookingService;
        this.invoiceService = invoiceService;
    }

    @KafkaListener(topics = "finamer-update-booking-balance", groupId = "invoicing-entity-replica")
    public void listen(UpdateBookingBalanceKafka objKafka) {
        try {

            ManageBookingDto bookingDto = this.bookingService.findById(objKafka.getId());
            Double minus = bookingDto.getDueAmount() - objKafka.getAmountBalance();
            bookingDto.setDueAmount(objKafka.getAmountBalance());
            this.bookingService.update(bookingDto);

            ManageInvoiceDto invoiceDto = bookingDto.getInvoice();
            invoiceDto.setDueAmount(invoiceDto.getDueAmount() - minus);
            this.invoiceService.update(invoiceDto);

        } catch (Exception ex) {
            Logger.getLogger(ConsumerUpdateBookingService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}

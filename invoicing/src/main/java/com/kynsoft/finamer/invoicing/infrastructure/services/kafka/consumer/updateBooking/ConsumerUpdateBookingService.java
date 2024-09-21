package com.kynsoft.finamer.invoicing.infrastructure.services.kafka.consumer.updateBooking;

import com.kynsof.share.core.domain.kafka.entity.update.UpdateBookingBalanceKafka;
import com.kynsoft.finamer.invoicing.domain.dto.ManageBookingDto;
import com.kynsoft.finamer.invoicing.domain.dto.ManageInvoiceDto;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.EInvoiceType;
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
            bookingDto.setDueAmount(bookingDto.getDueAmount() - objKafka.getAmountBalance());
            this.bookingService.update(bookingDto);

            ManageInvoiceDto invoiceDto = bookingDto.getInvoice();
            invoiceDto.setDueAmount(invoiceDto.getDueAmount() - objKafka.getAmountBalance());
            this.invoiceService.update(invoiceDto);

            if (invoiceDto.getInvoiceType().equals(EInvoiceType.CREDIT)) {
                ManageBookingDto bookingParent = bookingDto.getParent();
                bookingParent.setDueAmount(bookingParent.getDueAmount() + objKafka.getAmountBalance());
                this.bookingService.update(bookingParent);

                ManageInvoiceDto parent = this.invoiceService.findById(invoiceDto.getParent().getId());
                parent.setDueAmount(parent.getDueAmount() + objKafka.getAmountBalance());
                this.invoiceService.update(parent);
            }
        } catch (Exception ex) {
            Logger.getLogger(ConsumerUpdateBookingService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}

package com.kynsoft.finamer.invoicing.infrastructure.services.kafka.consumer.undoApplicationUpdateBooking;

import com.kynsof.share.core.domain.kafka.entity.update.UpdateBookingBalanceKafka;
import com.kynsoft.finamer.invoicing.domain.dto.ManageBookingDto;
import com.kynsoft.finamer.invoicing.domain.dto.ManageInvoiceDto;
import com.kynsoft.finamer.invoicing.domain.dto.PaymentDetailDto;
import com.kynsoft.finamer.invoicing.domain.services.IManageBookingService;
import com.kynsoft.finamer.invoicing.domain.services.IManageInvoiceService;
import com.kynsoft.finamer.invoicing.domain.services.IPaymentDetailService;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ConsumerUndoApplicationUpdateBookingService {

    private final IManageBookingService bookingService;
    private final IManageInvoiceService invoiceService;

    private final IPaymentDetailService detailService;

    public ConsumerUndoApplicationUpdateBookingService(IManageBookingService bookingService,
            IManageInvoiceService invoiceService,
            IPaymentDetailService detailService) {
        this.bookingService = bookingService;
        this.invoiceService = invoiceService;
        this.detailService = detailService;
    }

    @KafkaListener(topics = "finamer-undo-application-update-booking-balance", groupId = "invoicing-entity-replica")
    public void listen(UpdateBookingBalanceKafka objKafka) {
        try {

            ManageBookingDto bookingDto = this.bookingService.findById(objKafka.getId());
            bookingDto.setDueAmount(bookingDto.getDueAmount() + objKafka.getAmountBalance());
            this.bookingService.update(bookingDto);

            ManageInvoiceDto invoiceDto = this.invoiceService.findById(bookingDto.getInvoice().getId());
            invoiceDto.setDueAmount(invoiceDto.getDueAmount() + objKafka.getAmountBalance());
            this.invoiceService.update(invoiceDto);

            PaymentDetailDto update = this.detailService.findById(objKafka.getPaymentKafka().getDetails().getId());
            update.setManageBooking(null);
            this.detailService.update(update);

        } catch (Exception ex) {
            Logger.getLogger(ConsumerUndoApplicationUpdateBookingService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}

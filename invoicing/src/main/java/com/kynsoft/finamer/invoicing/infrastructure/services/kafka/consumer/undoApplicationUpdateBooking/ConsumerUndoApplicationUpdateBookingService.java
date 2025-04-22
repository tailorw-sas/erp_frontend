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

import java.util.*;
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
            ManageInvoiceDto invoice = this.invoiceService.findByBookingId(objKafka.getId());
            ManageBookingDto booking = this.getBookingById(invoice.getBookings(), objKafka.getId());
            if(Objects.nonNull(booking)){
                booking.setDueAmount(objKafka.getAmountBalance());
                this.setInvoiceDueAmount(invoice);

                this.invoiceService.update(invoice);
            }

            PaymentDetailDto paymentDetail = this.detailService.findById(objKafka.getPaymentKafka().getDetails().getId());
            paymentDetail.setManageBooking(null);
            this.detailService.update(paymentDetail);

        } catch (Exception ex) {
            Logger.getLogger(ConsumerUndoApplicationUpdateBookingService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private ManageBookingDto getBookingById(List<ManageBookingDto> bookings, UUID id){
        if(Objects.isNull(bookings)){
            return null;
        }

        return bookings.stream()
                .filter(booking -> booking.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    private void setInvoiceDueAmount(ManageInvoiceDto invoiceDto){
        Double currentDueAmount = invoiceDto.getBookings().stream()
                .mapToDouble(ManageBookingDto::getDueAmount)
                .sum();
        invoiceDto.setDueAmount(currentDueAmount);
    }

}

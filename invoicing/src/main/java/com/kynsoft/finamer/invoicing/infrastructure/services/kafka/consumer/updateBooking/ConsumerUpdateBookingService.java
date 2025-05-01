package com.kynsoft.finamer.invoicing.infrastructure.services.kafka.consumer.updateBooking;

import com.kynsof.share.core.domain.kafka.entity.update.UpdateBookingBalanceKafka;
import com.kynsoft.finamer.invoicing.domain.dto.ManageBookingDto;
import com.kynsoft.finamer.invoicing.domain.dto.ManageHotelDto;
import com.kynsoft.finamer.invoicing.domain.dto.ManageInvoiceDto;
import com.kynsoft.finamer.invoicing.domain.dto.PaymentDetailDto;
import com.kynsoft.finamer.invoicing.domain.dto.PaymentDto;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.EInvoiceType;
import com.kynsoft.finamer.invoicing.domain.services.IManageBookingService;
import com.kynsoft.finamer.invoicing.domain.services.IManageHotelService;
import com.kynsoft.finamer.invoicing.domain.services.IManageInvoiceService;
import com.kynsoft.finamer.invoicing.domain.services.IPaymentDetailService;
import com.kynsoft.finamer.invoicing.domain.services.IPaymentService;

import lombok.extern.java.Log;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ConsumerUpdateBookingService {

    private final IManageBookingService bookingService;
    private final IManageInvoiceService invoiceService;

    private final IPaymentService paymentService;
    private final IPaymentDetailService detailService;

    private final IManageHotelService manageHotelService;

    public ConsumerUpdateBookingService(IManageBookingService bookingService, 
                                        IManageInvoiceService invoiceService,
                                        IPaymentService paymentService, 
                                        IPaymentDetailService detailService,
                                        IManageHotelService manageHotelService) {
        this.bookingService = bookingService;
        this.invoiceService = invoiceService;
        this.paymentService = paymentService;
        this.detailService = detailService;
        this.manageHotelService = manageHotelService;
    }

    @KafkaListener(topics = "finamer-update-booking-balance", groupId = "invoicing-entity-replica")
    public void listen(UpdateBookingBalanceKafka objKafka) {
        try {
            ManageInvoiceDto invoice = this.invoiceService.findByBookingId(objKafka.getId());
            ManageBookingDto booking = this.getBookingById(invoice.getBookings(), objKafka.getId());
            if(Objects.nonNull(booking)){
                booking.setDueAmount(objKafka.getAmountBalance());
                booking.setUpdatedAt(LocalDateTime.now());
                this.setInvoiceDueAmount(invoice);

                this.invoiceService.update(invoice);

                PaymentDto payment = new PaymentDto(objKafka.getPaymentKafka().getId(), objKafka.getPaymentKafka().getPaymentId());
                this.paymentService.create(payment);
                this.detailService.create(new PaymentDetailDto(objKafka.getPaymentKafka().getDetails().getId(), objKafka.getPaymentKafka().getDetails().getPaymentDetailId(), payment, booking));

                ManageHotelDto hotelDto = this.manageHotelService.findById(invoice.getHotel().getId());
                if (invoice.getInvoiceType().equals(EInvoiceType.CREDIT) && !hotelDto.getAutoApplyCredit() && objKafka.isDeposit()) {
                    ManageBookingDto bookingParent = this.bookingService.findById(booking.getParent().getId());
                    bookingParent.setDueAmount(bookingParent.getDueAmount());
                    this.bookingService.update(bookingParent);

                    ManageInvoiceDto parent = this.invoiceService.findById(invoice.getParent().getId());
                    this.setInvoiceDueAmount(parent);
                    this.invoiceService.update(parent);
                }
            }else{
                Logger.getLogger(ConsumerUpdateBookingService.class.getName()).log(Level.SEVERE, "The booking not found or it is null");
            }
        } catch (Exception ex) {
            Logger.getLogger(ConsumerUpdateBookingService.class.getName()).log(Level.SEVERE, null, ex);
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

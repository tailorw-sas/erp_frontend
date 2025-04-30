package com.kynsoft.finamer.invoicing.infrastructure.services.kafka.consumer.updateBooking;

import com.kynsof.share.core.domain.kafka.entity.ReplicateBookingKafka;
import com.kynsof.share.core.domain.kafka.entity.update.UpdateBookingBalanceKafka;
import com.kynsof.share.utils.BankerRounding;
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

import jakarta.transaction.Transactional;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Service
public class ConsumerUpdateBookingService {

    private final IManageBookingService bookingService;
    private final IManageInvoiceService invoiceService;

    private final IPaymentService paymentService;
    private final IPaymentDetailService detailService;

    private final IManageHotelService manageHotelService;

    private static final Logger LOGGER =    Logger.getLogger(ConsumerUpdateBookingService.class.getName());

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
            if(Objects.isNull(objKafka) || objKafka.getBookingsKafka().isEmpty()){
                LOGGER.log(Level.INFO, "ConsumerUpdateBookingService -> The booking list is null or empty");
                return;
            }
            List<ManageInvoiceDto> invoicesToUpdate = this.getInvoices(objKafka);
            Map<UUID, ManageBookingDto> bookingsMap = mapBookingsById(invoicesToUpdate);
            Map<UUID, ManageHotelDto> hotelsMap = this.getHotelsMap(invoicesToUpdate);

            List<PaymentDto> paymentsToCreate = new ArrayList<>();
            List<PaymentDetailDto> paymentDetailsToCreate = new ArrayList<>();
            Set<ManageBookingDto> bookingParentsToUpdate = new HashSet<>();
            Set<ManageInvoiceDto> invoiceParentsToUpdate = new HashSet<>();

            for (ReplicateBookingKafka replicateBookingKafka : objKafka.getBookingsKafka()) {
                try{
                    ManageBookingDto booking = bookingsMap.get(replicateBookingKafka.getId());

                    if(booking == null){
                        LOGGER.log(Level.SEVERE, "The booking ID {0} not found", replicateBookingKafka.getId());
                        continue;
                    }

                    if (booking.getUpdatedAt() == null || replicateBookingKafka.getTimestamp().isAfter(booking.getUpdatedAt().atOffset(ZoneOffset.UTC))) {

                        booking.setDueAmount(BankerRounding.round(replicateBookingKafka.getAmountBalance()));
                        booking.setUpdatedAt(LocalDateTime.now());

                        ManageInvoiceDto invoice = getInvoiceFromListByBookingId(invoicesToUpdate, booking.getId());
                        setInvoiceDueAmount(invoice);

                        PaymentDto payment = new PaymentDto(
                                replicateBookingKafka.getPaymentKafka().getId(),
                                replicateBookingKafka.getPaymentKafka().getPaymentId()
                        );

                        PaymentDetailDto paymentDetail = new PaymentDetailDto(replicateBookingKafka.getPaymentKafka().getDetails().getId(),
                                replicateBookingKafka.getPaymentKafka().getDetails().getPaymentDetailId(),
                                payment, booking);

                        paymentsToCreate.add(payment);
                        paymentDetailsToCreate.add(paymentDetail);

                        ManageHotelDto hotelDto = hotelsMap.get(invoice.getHotel().getId());
                        if (invoice.getInvoiceType().equals(EInvoiceType.CREDIT) && !hotelDto.getAutoApplyCredit() && replicateBookingKafka.isDeposit()) {
                            //ManageBookingDto bookingParent = this.bookingService.findById(booking.getParent().getId());
                            //TODO: VALIDAR QUE LLEGUE COMPLETO EL BOOKING PADRE
                            double amountBalance = replicateBookingKafka.getAmountBalance() * -1;

                            //TODO Validar si actualizando el invocice para que se actualice correctamente.
                            ManageBookingDto bookingParent = booking.getParent();
                            if(bookingParent != null){
                                bookingParent.setDueAmount(BankerRounding.round(replicateBookingKafka.getAmountBalance()));
                                bookingParent.setUpdatedAt(LocalDateTime.now());
                                bookingParentsToUpdate.add(bookingParent);
                            }else{
                                LOGGER.log(Level.SEVERE, "The parent booking of booking ID {0} is null", booking.getId());
                            }

                            ManageInvoiceDto parent = invoice.getParent();
                            if(parent != null){
                                this.setInvoiceDueAmount(parent);
                                invoiceParentsToUpdate.add(parent);
                            }
                            else{
                                LOGGER.log(Level.SEVERE, "The parent invoice of invoice ID {0} is null", invoice.getId());
                            }
                        }
                    }
                }catch (Exception ex){
                    LOGGER.log(Level.SEVERE, "Error procesando booking " + replicateBookingKafka.getId(), ex);
                }
            }

            this.saveChanges(invoicesToUpdate, paymentsToCreate, paymentDetailsToCreate, bookingParentsToUpdate, invoiceParentsToUpdate);

        } catch (Exception ex) {
            Logger.getLogger(ConsumerUpdateBookingService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private List<ManageInvoiceDto> getInvoices(UpdateBookingBalanceKafka objKafka){
        List<UUID> bookingIds = objKafka.getBookingsKafka().stream()
                .map(ReplicateBookingKafka::getId)
                .distinct()
                .collect(Collectors.toList());
        return this.invoiceService.findInvoicesByBookingIds(bookingIds);
    }

    private void setInvoiceDueAmount(ManageInvoiceDto invoiceDto){
        Double currentDueAmount = invoiceDto.getBookings().stream()
                .mapToDouble(ManageBookingDto::getDueAmount)
                .sum();
        invoiceDto.setDueAmount(BankerRounding.round(currentDueAmount));
    }

    private ManageInvoiceDto getInvoiceFromListByBookingId(List<ManageInvoiceDto> invoices, UUID bookingId){
        return invoices.stream()
                .filter(invoice -> invoice.getBookings().stream()
                        .anyMatch(booking -> booking.getId().equals(bookingId)))
                .findFirst()
                .orElse(null);
    }

    private Map<UUID, ManageBookingDto> mapBookingsById(List<ManageInvoiceDto> invoices) {
        return invoices.stream()
                .flatMap(invoice -> invoice.getBookings().stream())
                .collect(Collectors.toMap(ManageBookingDto::getId, booking -> booking));
    }

    private Map<UUID, ManageHotelDto> getHotelsMap(List<ManageInvoiceDto> invoices) {
        return invoices.stream()
                .map(i -> i.getHotel().getId())
                .distinct()
                .collect(Collectors.toMap(Function.identity(), manageHotelService::findById));
    }

    @Transactional
    private void saveChanges(List<ManageInvoiceDto> invoices,
                             List<PaymentDto> payments,
                             List<PaymentDetailDto> paymentDetails,
                             Set<ManageBookingDto> bookingParents,
                             Set<ManageInvoiceDto> invoceParents){
        this.invoiceService.updateAll(invoices);
        this.paymentService.createAll(payments);
        this.detailService.createAll(paymentDetails);
        if(!bookingParents.isEmpty()){
            List<ManageBookingDto> bookingParentList = new ArrayList<>(bookingParents);
            this.bookingService.updateAll(bookingParentList);
        }
        if(!invoceParents.isEmpty()){
            List<ManageInvoiceDto> invoiceParentList = new ArrayList<>(invoceParents);
            this.invoiceService.updateAll(invoiceParentList);
        }
    }
}

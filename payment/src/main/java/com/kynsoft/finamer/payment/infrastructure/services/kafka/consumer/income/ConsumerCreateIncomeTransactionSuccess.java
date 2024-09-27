package com.kynsoft.finamer.payment.infrastructure.services.kafka.consumer.income;

import com.kynsof.share.core.domain.kafka.entity.CreateIncomeTransactionKafka;
import com.kynsof.share.core.domain.kafka.entity.CreateIncomeTransactionSuccessKafka;
import com.kynsof.share.core.domain.kafka.entity.ManageBookingKafka;
import com.kynsoft.finamer.payment.domain.dto.ManageBookingDto;
import com.kynsoft.finamer.payment.domain.dto.ManageInvoiceDto;
import com.kynsoft.finamer.payment.domain.dto.PaymentDetailDto;
import com.kynsoft.finamer.payment.domain.dtoEnum.EInvoiceType;
import com.kynsoft.finamer.payment.domain.services.IManageBookingService;
import com.kynsoft.finamer.payment.domain.services.IManageInvoiceService;
import com.kynsoft.finamer.payment.domain.services.IPaymentDetailService;
import com.kynsoft.finamer.payment.infrastructure.identity.ManageBooking;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
public class ConsumerCreateIncomeTransactionSuccess {
    private final IManageInvoiceService manageInvoiceService;
    private final IPaymentDetailService paymentDetailService;

    private final IManageBookingService manageBookingService;

    public ConsumerCreateIncomeTransactionSuccess(IManageInvoiceService manageInvoiceService,
                                                  IPaymentDetailService paymentDetailService, IManageBookingService manageBookingService) {
        this.manageInvoiceService = manageInvoiceService;
        this.paymentDetailService = paymentDetailService;
        this.manageBookingService = manageBookingService;
    }

    @KafkaListener(topics = "finamer-create-income-transaction-success", groupId = "income-entity-replica")
    public void listen(CreateIncomeTransactionSuccessKafka objKafka) {
            this.stablishRelationPaymentDetailsIncome(objKafka);
    }

    private void stablishRelationPaymentDetailsIncome(CreateIncomeTransactionSuccessKafka objKafka){
        ManageInvoiceDto manageInvoiceDto =  createManageInvoice(objKafka,createManageBooking(objKafka.getBookings()));
        manageInvoiceService.create(manageInvoiceDto);
        PaymentDetailDto paymentDetailDto =paymentDetailService.findById(objKafka.getRelatedPaymentDetailIds());
        paymentDetailDto.setManageBooking(manageInvoiceDto.getBookings().get(0));
        paymentDetailService.create(paymentDetailDto);
    }

    private List<ManageBookingDto> createManageBooking(List<ManageBookingKafka> manageBookingKafkas){
      return   manageBookingKafkas.stream().map(booking-> new ManageBookingDto(booking.getId(),booking.getBookingId(),
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
               null,
               Objects.nonNull(booking.getBookingParent())?manageBookingService.findById(booking.getBookingParent()):null
       )).toList();
    }

    private ManageInvoiceDto createManageInvoice(CreateIncomeTransactionSuccessKafka objKafka,List<ManageBookingDto> bookingDtos){
        return new ManageInvoiceDto(
                objKafka.getId(),
                objKafka.getInvoiceId(),
                objKafka.getInvoiceNo(),
                deleteHotelInfo(objKafka.getInvoiceNumber()),
                EInvoiceType.valueOf(objKafka.getInvoiceType()),
                objKafka.getInvoiceAmount(),
                bookingDtos,
                false,
                objKafka.getInvoiceParent() != null ? this.manageInvoiceService.findById(objKafka.getInvoiceParent()) : null,
                objKafka.getInvoiceDate()
        );
    }

    private String deleteHotelInfo(String input) {
        return input.replaceAll("-(.*?)-", "-");
    }
}

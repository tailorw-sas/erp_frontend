package com.kynsoft.finamer.payment.application.services.paymentDetail.apply;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.exception.BusinessNotFoundException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.exception.GlobalBusinessException;
import com.kynsof.share.core.domain.http.entity.BookingHttp;
import com.kynsof.share.core.domain.kafka.entity.ReplicateBookingKafka;
import com.kynsof.share.core.domain.kafka.entity.ReplicatePaymentDetailsKafka;
import com.kynsof.share.core.domain.kafka.entity.ReplicatePaymentKafka;
import com.kynsof.share.core.domain.kafka.entity.update.UpdateBookingBalanceKafka;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.rules.ValidateObjectNotNullRule;
import com.kynsof.share.core.infrastructure.util.DateUtil;
import com.kynsoft.finamer.payment.application.command.paymentDetail.applyPayment.ApplyPaymentDetailCommand;
import com.kynsoft.finamer.payment.domain.core.applyPayment.ProcessApplyPaymentDetail;
import com.kynsoft.finamer.payment.domain.dto.ManageBookingDto;
import com.kynsoft.finamer.payment.domain.dto.PaymentCloseOperationDto;
import com.kynsoft.finamer.payment.domain.dto.PaymentDetailDto;
import com.kynsoft.finamer.payment.domain.dto.PaymentDto;
import com.kynsoft.finamer.payment.domain.dtoEnum.EInvoiceType;
import com.kynsoft.finamer.payment.domain.services.IManageBookingService;
import com.kynsoft.finamer.payment.domain.services.IPaymentCloseOperationService;
import com.kynsoft.finamer.payment.domain.services.IPaymentDetailService;
import com.kynsoft.finamer.payment.domain.services.IPaymentService;
import com.kynsoft.finamer.payment.infrastructure.services.http.BookingHttpUUIDService;
import com.kynsoft.finamer.payment.infrastructure.services.http.helper.BookingImportAutomaticeHelperServiceImpl;
import com.kynsoft.finamer.payment.infrastructure.services.kafka.producer.updateBooking.ProducerUpdateBookingService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ApplyPaymentDetailService {

    private final IPaymentDetailService paymentDetailService;
    private final IManageBookingService manageBookingService;
    private final IPaymentService paymentService;
    private final ProducerUpdateBookingService producerUpdateBookingService;
    private final IPaymentCloseOperationService paymentCloseOperationService;
    private final BookingImportAutomaticeHelperServiceImpl bookingImportAutomaticeHelperServiceImpl;
    private final BookingHttpUUIDService bookingHttpUUIDService;

    public ApplyPaymentDetailService(IPaymentDetailService paymentDetailService,
                                     IManageBookingService manageBookingService,
                                     IPaymentService paymentService,
                                     ProducerUpdateBookingService producerUpdateBookingService,
                                     IPaymentCloseOperationService paymentCloseOperationService,
                                     BookingImportAutomaticeHelperServiceImpl bookingImportAutomaticeHelperServiceImpl,
                                     BookingHttpUUIDService bookingHttpUUIDService){
        this.paymentDetailService = paymentDetailService;
        this.manageBookingService = manageBookingService;
        this.paymentService = paymentService;
        this.producerUpdateBookingService = producerUpdateBookingService;
        this.paymentCloseOperationService = paymentCloseOperationService;
        this.bookingImportAutomaticeHelperServiceImpl = bookingImportAutomaticeHelperServiceImpl;
        this.bookingHttpUUIDService = bookingHttpUUIDService;
    }

    @Transactional
    public PaymentDto applyDetail(ApplyPaymentDetailCommand command){
        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(command.getBooking(), "id", "Booking ID cannot be null."));
        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(command.getPaymentDetail(), "id", "Payment Detail ID cannot be null."));

        ManageBookingDto booking = this.getBookingDto(command.getBooking());
        PaymentDetailDto paymentDetail = this.paymentDetailService.findById(command.getPaymentDetail());
        PaymentDto payment = paymentDetail.getPayment();
        OffsetDateTime transactionDate = this.getTransactionDate(payment.getHotel().getId());

        ProcessApplyPaymentDetail processApplyPaymentDetail = new ProcessApplyPaymentDetail(payment,
                paymentDetail,
                booking,
                transactionDate,
                paymentDetail.getAmount());
        processApplyPaymentDetail.process();

        this.saveAndReplicateBooking(payment, paymentDetail, booking);

        return payment;
    }

    private OffsetDateTime getTransactionDate(UUID hotel) {
        PaymentCloseOperationDto closeOperationDto = this.paymentCloseOperationService.findByHotelIds(hotel);

        if (DateUtil.getDateForCloseOperation(closeOperationDto.getBeginDate(), closeOperationDto.getEndDate())) {
            return OffsetDateTime.now(ZoneId.of("UTC"));
        }
        return OffsetDateTime.of(closeOperationDto.getEndDate(), LocalTime.now(ZoneId.of("UTC")), ZoneOffset.UTC);
    }

    private ManageBookingDto getBookingDto(UUID bookingId) {
        try {
            return this.manageBookingService.findById(bookingId);
        } catch (Exception e) {
            try {
                /***
                 * TODO: Aqui se define un flujo alternativo por HTTP si en algun momento kafka falla y las invoice no se replicaron, para
                 * evitar que el flujo de aplicacion de pago falle.
                 */
                BookingHttp bookingHttp = this.bookingHttpUUIDService.sendGetBookingHttpRequest(bookingId);
                this.bookingImportAutomaticeHelperServiceImpl.createInvoice(bookingHttp);
                return this.manageBookingService.findById(bookingId);
            } catch (Exception ex) {
                //FLUJO PARA ESPERAR MIENTRAS LAS BD SE SINCRONIZAN.
                int maxAttempts = 3;
                while (maxAttempts > 0) {
                    try {
                        return this.manageBookingService.findById(bookingId);
                    } catch (Exception exc) {
                    }
                    maxAttempts--;
                    try {
                        TimeUnit.SECONDS.sleep(1);
                    } catch (InterruptedException excp) {
                    }
                }
                throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.BOOKING_NOT_FOUND, new ErrorField("booking Id", DomainErrorMessage.BOOKING_NOT_FOUND.getReasonPhrase())));
            }
        }
    }

    private void saveAndReplicateBooking(PaymentDto payment, PaymentDetailDto paymentDetail, ManageBookingDto booking){
        this.manageBookingService.update(booking);
        this.paymentDetailService.update(paymentDetail);//TODO Cambiar el metodo update para que devuelva el id generado del booking
        this.paymentService.update(payment);

        try {
            ReplicatePaymentKafka paymentKafka = new ReplicatePaymentKafka(
                    payment.getId(),
                    payment.getPaymentId(),
                    new ReplicatePaymentDetailsKafka(paymentDetail.getId(), paymentDetail.getPaymentDetailId()
                    ));
            if (booking.getInvoice().getInvoiceType().equals(EInvoiceType.CREDIT) || booking.getInvoice().getInvoiceType().equals(EInvoiceType.OLD_CREDIT)) {
                ReplicateBookingKafka replicateBookingKafka = new ReplicateBookingKafka(booking.getId(), booking.getAmountBalance(), paymentKafka, false, OffsetDateTime.now());
                this.producerUpdateBookingService.update(new UpdateBookingBalanceKafka(List.of(replicateBookingKafka)));
            } else {
                ReplicateBookingKafka replicateBookingKafka = new ReplicateBookingKafka(booking.getId(), booking.getAmountBalance(), paymentKafka, false, OffsetDateTime.now());
                this.producerUpdateBookingService.update(new UpdateBookingBalanceKafka(List.of(replicateBookingKafka)));
            }
        } catch (Exception ex) {
            Logger.getLogger(ApplyPaymentDetailService.class.getName()).log(Level.SEVERE, "Error at replicating booking", ex);
        }
    }
}

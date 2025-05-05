package com.kynsoft.finamer.payment.application.services.paymentDetail.apply;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.exception.BusinessNotFoundException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.exception.GlobalBusinessException;
import com.kynsof.share.core.domain.http.entity.BookingHttp;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.rules.ValidateObjectNotNullRule;
import com.kynsof.share.core.infrastructure.util.DateUtil;
import com.kynsoft.finamer.payment.domain.core.applyPayment.ProcessApplyPaymentDetail;
import com.kynsoft.finamer.payment.domain.dto.ManageBookingDto;
import com.kynsoft.finamer.payment.domain.dto.PaymentCloseOperationDto;
import com.kynsoft.finamer.payment.domain.dto.PaymentDetailDto;
import com.kynsoft.finamer.payment.domain.dto.PaymentDto;
import com.kynsoft.finamer.payment.domain.services.IManageBookingService;
import com.kynsoft.finamer.payment.domain.services.IPaymentCloseOperationService;
import com.kynsoft.finamer.payment.domain.services.IPaymentDetailService;
import com.kynsoft.finamer.payment.domain.services.IPaymentService;
import com.kynsoft.finamer.payment.infrastructure.services.http.BookingHttpUUIDService;
import com.kynsoft.finamer.payment.infrastructure.services.http.helper.BookingImportAutomaticeHelperServiceImpl;
import jakarta.transaction.Transactional;
import lombok.Getter;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class ApplyPaymentDetailService {

    private final IPaymentDetailService paymentDetailService;
    private final IManageBookingService manageBookingService;
    private final IPaymentService paymentService;
    private final IPaymentCloseOperationService paymentCloseOperationService;
    private final BookingImportAutomaticeHelperServiceImpl bookingImportAutomaticeHelperServiceImpl;
    private final BookingHttpUUIDService bookingHttpUUIDService;

    @Getter
    private PaymentDto payment;

    @Getter
    private PaymentDetailDto paymentDetail;

    @Getter
    private ManageBookingDto booking;

    public ApplyPaymentDetailService(IPaymentDetailService paymentDetailService,
                                     IManageBookingService manageBookingService,
                                     IPaymentService paymentService,
                                     IPaymentCloseOperationService paymentCloseOperationService,
                                     BookingImportAutomaticeHelperServiceImpl bookingImportAutomaticeHelperServiceImpl,
                                     BookingHttpUUIDService bookingHttpUUIDService){
        this.paymentDetailService = paymentDetailService;
        this.manageBookingService = manageBookingService;
        this.paymentService = paymentService;
        this.paymentCloseOperationService = paymentCloseOperationService;
        this.bookingImportAutomaticeHelperServiceImpl = bookingImportAutomaticeHelperServiceImpl;
        this.bookingHttpUUIDService = bookingHttpUUIDService;
    }

    @Transactional
    public PaymentDto applyDetail(UUID paymentDetailId,
                                  UUID bookingId,
                                  UUID employeeId){
        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(bookingId, "id", "Booking ID cannot be null."));
        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(paymentDetailId, "id", "Payment Detail ID cannot be null."));

        this.booking = this.getBookingDto(bookingId);
        this.paymentDetail = this.paymentDetailService.findById(paymentDetailId);
        this.payment = this.paymentDetail.getPayment();
        OffsetDateTime transactionDate = this.getTransactionDate(this.payment.getHotel().getId());

        ProcessApplyPaymentDetail processApplyPaymentDetail = new ProcessApplyPaymentDetail(this.payment,
                this.paymentDetail,
                this.booking,
                transactionDate,
                this.paymentDetail.getAmount());
        processApplyPaymentDetail.process();

        this.saveChanges(this.payment, this.paymentDetail, this.booking);

        return this.payment;
    }

    private OffsetDateTime getTransactionDate(UUID hotel) {
        PaymentCloseOperationDto closeOperationDto = this.paymentCloseOperationService.findByHotelId(hotel);

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

    private void saveChanges(PaymentDto payment, PaymentDetailDto paymentDetail, ManageBookingDto booking){
        this.manageBookingService.update(booking);
        PaymentDetailDto createdPaymentDetail = this.paymentDetailService.update(paymentDetail);
        paymentDetail.setPaymentDetailId(createdPaymentDetail.getPaymentDetailId());
        this.paymentService.update(payment);
    }
}

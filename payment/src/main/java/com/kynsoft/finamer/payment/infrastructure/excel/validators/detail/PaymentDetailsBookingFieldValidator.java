package com.kynsoft.finamer.payment.infrastructure.excel.validators.detail;

import com.kynsof.share.core.application.excel.validator.ExcelRuleValidator;
import com.kynsof.share.core.domain.exception.BusinessNotFoundException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.exception.GlobalBusinessException;
import com.kynsof.share.core.domain.http.entity.BookingHttp;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsoft.finamer.payment.domain.dto.ManageBookingDto;
import com.kynsoft.finamer.payment.domain.excel.bean.detail.PaymentDetailRow;
import com.kynsoft.finamer.payment.domain.services.IManageBookingService;
import com.kynsoft.finamer.payment.infrastructure.services.http.BookingHttpGenIdService;
import com.kynsoft.finamer.payment.infrastructure.services.http.helper.BookingImportAutomaticeHelperServiceImpl;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Component
public class PaymentDetailsBookingFieldValidator extends ExcelRuleValidator<PaymentDetailRow> {

    private final IManageBookingService bookingService;
    private final BookingImportAutomaticeHelperServiceImpl bookingImportAutomaticeHelperServiceImpl;
    private final BookingHttpGenIdService bookingHttpGenIdService;

    protected PaymentDetailsBookingFieldValidator(ApplicationEventPublisher applicationEventPublisher,
            IManageBookingService bookingService,
            BookingImportAutomaticeHelperServiceImpl bookingImportAutomaticeHelperServiceImpl,
            BookingHttpGenIdService bookingHttpGenIdService) {
        super(applicationEventPublisher);
        this.bookingService = bookingService;
        this.bookingImportAutomaticeHelperServiceImpl = bookingImportAutomaticeHelperServiceImpl;
        this.bookingHttpGenIdService = bookingHttpGenIdService;
    }

    @Override
    public boolean validate(PaymentDetailRow obj, List<ErrorField> errorFieldList) {
        if (!Objects.isNull(obj.getBookId())) {
            try {
                try {
//                    ManageBookingDto bookingDto = this.getBookingDto(Long.valueOf(obj.getBookId()));
//                    if (bookingDto.getAmountBalance() == 0) {
//                        errorFieldList.add(new ErrorField("bookingId", "The value of the booking balance is zero."));
//                        return false;
//                    }
                } catch (Exception e) {
                    errorFieldList.add(new ErrorField("bookingId", "The booking not exist."));
                    return false;
                }
            } catch (Exception e) {
                errorFieldList.add(new ErrorField("bookingId", "The booking not exist."));
                return false;
            }
        }
        return true;
    }

    private ManageBookingDto getBookingDto(Long bookingId) {
        try {
            return this.bookingService.findByGenId(bookingId);
        } catch (Exception e) {
            try {
                BookingHttp bookingHttp = this.bookingHttpGenIdService.sendGetBookingHttpRequest(bookingId);
                this.bookingImportAutomaticeHelperServiceImpl.createInvoice(bookingHttp);
                return this.bookingService.findByGenId(bookingId);
            } catch (Exception ex) {
                //FLUJO PARA ESPERAR MIENTRAS LAS BD SE SINCRONIZAN.
                int maxAttempts = 3;
                while (maxAttempts > 0) {
                    try {
                        return this.bookingService.findByGenId(bookingId);
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

}

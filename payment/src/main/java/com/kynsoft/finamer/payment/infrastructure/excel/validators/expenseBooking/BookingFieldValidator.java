package com.kynsoft.finamer.payment.infrastructure.excel.validators.expenseBooking;

import com.kynsof.share.core.application.excel.validator.ExcelRuleValidator;
import com.kynsof.share.core.domain.exception.BusinessNotFoundException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.exception.GlobalBusinessException;
import com.kynsof.share.core.domain.http.entity.BookingHttp;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsoft.finamer.payment.domain.dto.ManageBookingDto;
import com.kynsoft.finamer.payment.domain.dto.ManageInvoiceDto;
import com.kynsoft.finamer.payment.domain.dto.projection.booking.BookingProjectionSimple;
import com.kynsoft.finamer.payment.domain.dtoEnum.EInvoiceType;
import com.kynsoft.finamer.payment.domain.excel.bean.payment.PaymentExpenseBookingRow;
import com.kynsoft.finamer.payment.domain.services.IManageBookingService;
import com.kynsoft.finamer.payment.infrastructure.services.http.BookingHttpGenIdService;
import com.kynsoft.finamer.payment.infrastructure.services.http.helper.BookingImportAutomaticeHelperServiceImpl;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Component
public class BookingFieldValidator extends ExcelRuleValidator<PaymentExpenseBookingRow> {

    private final IManageBookingService bookingService;
    private final BookingHttpGenIdService bookingHttpGenIdService;
    private final BookingImportAutomaticeHelperServiceImpl bookingImportAutomaticeHelperServiceImpl;

    protected BookingFieldValidator(ApplicationEventPublisher applicationEventPublisher,
                                    IManageBookingService bookingService,
                                    BookingHttpGenIdService bookingHttpGenIdService,
                                    BookingImportAutomaticeHelperServiceImpl bookingImportAutomaticeHelperServiceImpl) {
        super(applicationEventPublisher);
        this.bookingService = bookingService;
        this.bookingHttpGenIdService = bookingHttpGenIdService;
        this.bookingImportAutomaticeHelperServiceImpl = bookingImportAutomaticeHelperServiceImpl;
    }

    @Override
    public boolean validate(PaymentExpenseBookingRow obj, List<ErrorField> errorFieldList) {
        if (Objects.isNull(obj.getBookingId())) {
            errorFieldList.add(new ErrorField("bookingId", "Booking id can't be empty"));
            return false;
        }
        try {
//            if (!bookingService.exitBookingByGenId(Long.parseLong(obj.getBookingId()))) {
//                errorFieldList.add(new ErrorField("bookingId", "The booking not exist"));
//                return false;
//            }
            //ManageBookingDto manageBooking = bookingService.findByGenId(Long.parseLong(obj.getBookingId()));
            //ManageBookingDto manageBooking = this.getBookingDto(Long.valueOf(obj.getBookingId()));
            BookingProjectionSimple manageBooking = this.getBookingDto(Long.valueOf(obj.getBookingId()));
            if (!EInvoiceType.INVOICE.equals(manageBooking.getInvoiceType())) {
                errorFieldList.add(new ErrorField("bookingId", "The invoice related isn't invoice type"));
                return false;
            }
            //ManageInvoiceDto manageInvoiceDto = manageBooking.getInvoice();
            //if (!obj.getHotelId().equals(manageInvoiceDto.getHotel().getId().toString())) {
            if (!obj.getHotelId().equals(manageBooking.getInvoiceHotel().toString())) {
                errorFieldList.add(new ErrorField("bookingId", "Booking don't belong to the selected hotel"));
                return false;
            }
        } catch (Exception e) {
            errorFieldList.add(new ErrorField("bookingId", "The booking not exist"));
            return false;
        }
        return true;
    }
    
    private BookingProjectionSimple getBookingDto(Long bookingId) {
        try {
            return this.bookingService.findSimpleDetailByGenId(bookingId);
            //return this.bookingService.findByGenId(bookingId);
        } catch (Exception e) {
            try {
                BookingHttp bookingHttp = this.bookingHttpGenIdService.sendGetBookingHttpRequest(bookingId);
                this.bookingImportAutomaticeHelperServiceImpl.createInvoice(bookingHttp);
                //return this.bookingService.findByGenId(bookingId);
                return this.bookingService.findSimpleDetailByGenId(bookingId);
            } catch (Exception ex) {
                //FLUJO PARA ESPERAR MIENTRAS LAS BD SE SINCRONIZAN.
                int maxAttempts = 3;
                while (maxAttempts > 0) {
                    try {
                        //return this.bookingService.findByGenId(bookingId);
                        return this.bookingService.findSimpleDetailByGenId(bookingId);
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

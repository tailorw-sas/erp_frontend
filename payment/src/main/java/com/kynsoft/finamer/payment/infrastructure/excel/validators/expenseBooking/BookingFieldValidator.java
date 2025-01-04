package com.kynsoft.finamer.payment.infrastructure.excel.validators.expenseBooking;

import com.kynsof.share.core.application.excel.validator.ExcelRuleValidator;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsoft.finamer.payment.domain.dto.ManageBookingDto;
import com.kynsoft.finamer.payment.domain.dto.ManageInvoiceDto;
import com.kynsoft.finamer.payment.domain.dtoEnum.EInvoiceType;
import com.kynsoft.finamer.payment.domain.excel.bean.payment.PaymentExpenseBookingRow;
import com.kynsoft.finamer.payment.domain.services.IManageBookingService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
public class BookingFieldValidator extends ExcelRuleValidator<PaymentExpenseBookingRow> {

    private final IManageBookingService bookingService;

    protected BookingFieldValidator(ApplicationEventPublisher applicationEventPublisher,
                                    IManageBookingService bookingService) {
        super(applicationEventPublisher);
        this.bookingService = bookingService;
    }

    @Override
    public boolean validate(PaymentExpenseBookingRow obj, List<ErrorField> errorFieldList) {
        if (Objects.isNull(obj.getBookingId())){
            errorFieldList.add(new ErrorField("bookingId","Booking id can't be empty"));
            return false;
        }
        if (!bookingService.exitBookingByGenId(Long.parseLong(obj.getBookingId()))){
            errorFieldList.add(new ErrorField("bookingId","The booking not exist"));
            return false;
        }
        ManageBookingDto manageBooking = bookingService.findByGenId(Long.parseLong(obj.getBookingId()));
        if (!EInvoiceType.INVOICE.equals(manageBooking.getInvoice().getInvoiceType())){
            errorFieldList.add(new ErrorField("bookingId","The invoice related isn't invoice type"));
            return false;
        }
        ManageInvoiceDto manageInvoiceDto = manageBooking.getInvoice();
        if (!obj.getHotelId().equals(manageInvoiceDto.getHotel().getId().toString())){
            errorFieldList.add(new ErrorField("bookingId","Booking don't belong to the selected hotel"));
            return false;
        }
        return true;
    }
}

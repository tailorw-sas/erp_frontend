package com.kynsoft.finamer.payment.infrastructure.excel.validators.expenseBooking;

import com.kynsof.share.core.application.excel.validator.ExcelRuleValidator;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsoft.finamer.payment.domain.dto.ManageBookingDto;
import com.kynsoft.finamer.payment.domain.excel.bean.payment.PaymentExpenseBookingRow;
import com.kynsoft.finamer.payment.domain.services.IManageBookingService;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;
import java.util.Objects;

public class BookingBalanceValidator extends ExcelRuleValidator<PaymentExpenseBookingRow> {

    private final IManageBookingService bookingService;
    protected BookingBalanceValidator(ApplicationEventPublisher applicationEventPublisher, IManageBookingService bookingService) {
        super(applicationEventPublisher);
        this.bookingService = bookingService;
    }

    @Override
    public boolean validate(PaymentExpenseBookingRow obj, List<ErrorField> errorFieldList) {
        if(Objects.isNull(obj.getBalance())){
            errorFieldList.add(new ErrorField("balance","Balance field can't be empty"));
            return false;
        }
        if (Objects.nonNull(obj.getBookingId()) && bookingService.exitBookingByGenId(Long.parseLong(obj.getBookingId()))){
            ManageBookingDto manageBookingDto = bookingService.findByGenId(Long.parseLong(obj.getBookingId()));
            if (obj.getBalance()>manageBookingDto.getAmountBalance()){
                errorFieldList.add(new ErrorField("balance","Balance can't be greater than Booking balance"));
                return false;
            }
        }

        return true;
    }
}

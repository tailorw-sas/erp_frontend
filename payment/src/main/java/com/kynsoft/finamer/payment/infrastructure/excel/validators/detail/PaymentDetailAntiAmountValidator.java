package com.kynsoft.finamer.payment.infrastructure.excel.validators.detail;

import com.kynsof.share.core.application.excel.validator.ExcelRuleValidator;
import com.kynsof.share.core.application.excel.validator.ICache;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsoft.finamer.payment.domain.dto.ManageBookingDto;
import com.kynsoft.finamer.payment.domain.excel.Cache;
import com.kynsoft.finamer.payment.domain.excel.bean.detail.PaymentDetailRow;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;
import java.util.Objects;

public class PaymentDetailAntiAmountValidator extends ExcelRuleValidator<PaymentDetailRow> {

    protected PaymentDetailAntiAmountValidator(ApplicationEventPublisher applicationEventPublisher) {
        super(applicationEventPublisher);
    }

    @Override
    public boolean validate(PaymentDetailRow obj, List<ErrorField> errorFieldList) {
        return true;
    }

    @Override
    public boolean validate(PaymentDetailRow obj, List<ErrorField> errorFieldList, ICache icache) {
        Cache cache = (Cache)icache;

        ManageBookingDto booking = getBooking(obj, cache);
        if(Objects.isNull(booking)){
            errorFieldList.add(new ErrorField("Booking", "The booking not found or is duplicated"));
            return false;
        }

        if(obj.getBalance() >= booking.getAmountBalance()){
            errorFieldList.add(new ErrorField("Booking Balance", "The selected transaction amount must be less or equal than the invoice balance"));
            return false;
        }

        return true;
    }

    private ManageBookingDto getBooking(PaymentDetailRow obj, Cache cache){
        if(Objects.nonNull(obj.getBookId())){
            return cache.getBooking(Long.parseLong(obj.getBookId()));
        }

        if(Objects.nonNull(obj.getCoupon())){
            List<ManageBookingDto> bookingsByCoupon = cache.getBookingsByCoupon(obj.getCoupon());
            if(bookingsByCoupon.isEmpty()){
                return null;
            }

            if(bookingsByCoupon.size() > 1){
                return null;
            }

            return bookingsByCoupon.get(0);
        }

        return null;
    }
}

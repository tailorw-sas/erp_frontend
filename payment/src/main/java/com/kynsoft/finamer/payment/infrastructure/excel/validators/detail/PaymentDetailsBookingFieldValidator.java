package com.kynsoft.finamer.payment.infrastructure.excel.validators.detail;

import com.kynsof.share.core.application.excel.validator.ExcelRuleValidator;
import com.kynsof.share.core.domain.exception.BusinessNotFoundException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.exception.GlobalBusinessException;
import com.kynsof.share.core.domain.http.entity.BookingHttp;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsoft.finamer.payment.domain.dto.ManageBookingDto;
import com.kynsoft.finamer.payment.domain.excel.Cache;
import com.kynsoft.finamer.payment.domain.excel.bean.detail.PaymentDetailRow;
import com.kynsoft.finamer.payment.domain.services.IManageBookingService;
import com.kynsoft.finamer.payment.infrastructure.services.http.BookingHttpGenIdService;
import com.kynsoft.finamer.payment.infrastructure.services.http.helper.BookingImportAutomaticeHelperServiceImpl;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class PaymentDetailsBookingFieldValidator extends ExcelRuleValidator<PaymentDetailRow> {

    private final Cache cache;

    protected PaymentDetailsBookingFieldValidator(ApplicationEventPublisher applicationEventPublisher,
                                                  Cache cache) {
        super(applicationEventPublisher);
        this.cache = cache;
    }

    @Override
    public boolean validate(PaymentDetailRow obj, List<ErrorField> errorFieldList) {
        if(Objects.nonNull(obj.getBookId())){
            if(Objects.isNull(this.cache.getBooking(Long.parseLong(obj.getBookId())))){
                errorFieldList.add(new ErrorField("bookingId", "The booking not exist."));
                return false;
            }
            return true;
        }

        if (Objects.isNull(obj.getCoupon()) || obj.getCoupon().trim().isEmpty()) {
            errorFieldList.add(new ErrorField("Cuopon", "The cuopon field must not be empty"));
            return false;
        }
        return true;
    }
}

package com.kynsoft.finamer.payment.infrastructure.excel.validators.detail;

import com.kynsof.share.core.application.excel.validator.ExcelRuleValidator;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsoft.finamer.payment.domain.dto.ManageBookingDto;
import com.kynsoft.finamer.payment.domain.excel.bean.detail.PaymentDetailRow;
import com.kynsoft.finamer.payment.domain.services.IManageBookingService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
public class PaymentDetailsBookingFieldValidator extends ExcelRuleValidator<PaymentDetailRow> {

    private final IManageBookingService bookingService;

    protected PaymentDetailsBookingFieldValidator(ApplicationEventPublisher applicationEventPublisher,
            IManageBookingService bookingService) {
        super(applicationEventPublisher);
        this.bookingService = bookingService;
    }

    @Override
    public boolean validate(PaymentDetailRow obj, List<ErrorField> errorFieldList) {
        if (!Objects.isNull(obj.getBookId())) {
            try {
                ManageBookingDto bookingDto = bookingService.findByGenId(Long.parseLong(obj.getBookId()));
                if (bookingDto.getAmountBalance() < obj.getBalance()) {
                    errorFieldList.add(new ErrorField("bookingId", "The amount to apply is greater than the balance of the booking."));
                    return false;
                }
            } catch (Exception e) {
                errorFieldList.add(new ErrorField("bookingId", "The booking not exist."));
                return false;
            }
        }
        return true;
    }
}

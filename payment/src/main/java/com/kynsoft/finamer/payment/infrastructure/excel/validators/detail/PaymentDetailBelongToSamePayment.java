package com.kynsoft.finamer.payment.infrastructure.excel.validators.detail;

import com.kynsof.share.core.application.excel.validator.ExcelRuleValidator;
import com.kynsof.share.core.application.excel.validator.ICache;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsoft.finamer.payment.domain.dto.PaymentDto;
import com.kynsoft.finamer.payment.domain.excel.Cache;
import com.kynsoft.finamer.payment.domain.excel.bean.detail.PaymentDetailRow;
import com.kynsoft.finamer.payment.domain.services.IPaymentService;
import lombok.Setter;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Setter
public class PaymentDetailBelongToSamePayment extends ExcelRuleValidator<PaymentDetailRow> {

    private final Cache cache;

    protected PaymentDetailBelongToSamePayment(ApplicationEventPublisher applicationEventPublisher,
                                               Cache cache) {
        super(applicationEventPublisher);
        this.cache = cache;
    }

    @Override
    public boolean validate(PaymentDetailRow obj, List<ErrorField> errorFieldList) {
        PaymentDto paymentDto = this.cache.getPaymentByPaymentId(Long.parseLong(obj.getPaymentId()));
        if(Objects.nonNull(paymentDto)){
            if (paymentDto.getPaymentId() != Long.parseLong(obj.getPaymentId())) {
                errorFieldList.add(new ErrorField("Payment Id", "The paymentId of the file doesn't match with the select payment"));
                return false;
            }
        }else{
            errorFieldList.add(new ErrorField("Payment Id", "The paymentId not found."));
            return false;
        }
        return true;
    }
}

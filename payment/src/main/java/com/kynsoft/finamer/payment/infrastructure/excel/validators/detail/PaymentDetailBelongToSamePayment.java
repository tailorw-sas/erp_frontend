package com.kynsoft.finamer.payment.infrastructure.excel.validators.detail;

import com.kynsof.share.core.application.excel.validator.ExcelRuleValidator;
import com.kynsof.share.core.application.excel.validator.ICache;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsoft.finamer.payment.domain.dto.PaymentDetailDto;
import com.kynsoft.finamer.payment.domain.dto.PaymentDto;
import com.kynsoft.finamer.payment.domain.dto.projection.PaymentProjectionSimple;
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

    private UUID paymentId;
    private final IPaymentService paymentService;

    protected PaymentDetailBelongToSamePayment(ApplicationEventPublisher applicationEventPublisher, IPaymentService paymentService) {
        super(applicationEventPublisher);
        this.paymentService = paymentService;
    }

    @Override
    public boolean validate(PaymentDetailRow obj, List<ErrorField> errorFieldList) {
        return true;
    }

    @Override
    public boolean validate(PaymentDetailRow obj, List<ErrorField> errorFieldList, ICache iCache) {
        Cache cache = (Cache) iCache;

        if (Objects.nonNull(paymentId)) {
            PaymentDto paymentDto = cache.getPaymentByPaymentId(Long.parseLong(obj.getPaymentId()));
            if(Objects.nonNull(paymentDto)){
                //PaymentDto paymentDto = paymentService.findById(paymentId);
                //PaymentProjection paymentDto = this.paymentService.findByPaymentIdProjection(Long.parseLong(obj.getPaymentId()));
                //PaymentProjectionSimple paymentDto = paymentService.findPaymentIdCacheable(Long.parseLong(obj.getPaymentId()));

                if (paymentDto.getPaymentId() != Long.parseLong(obj.getPaymentId())) {
                    errorFieldList.add(new ErrorField("Payment Id", "The paymentId of the file doesn't match with the select payment"));
                    return false;
                }
            }else{
                errorFieldList.add(new ErrorField("Payment Id", "The paymentId not found."));
                return false;
            }
        }
        return true;
    }
}

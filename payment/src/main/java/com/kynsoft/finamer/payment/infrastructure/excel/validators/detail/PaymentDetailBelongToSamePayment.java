package com.kynsoft.finamer.payment.infrastructure.excel.validators.detail;

import com.kynsof.share.core.application.excel.validator.ExcelRuleValidator;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsoft.finamer.payment.domain.dto.PaymentDto;
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
        if (Objects.nonNull(paymentId)) {
            PaymentDto paymentDto = paymentService.findById(paymentId);
           if (paymentDto.getPaymentId() != Long.parseLong(obj.getPaymentId())){
               errorFieldList.add(new ErrorField("Payment Id","The paymentId of the file doesn't match with the select payment"));
               return false;
           }
        }
        return true;
    }
}

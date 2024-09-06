package com.kynsoft.finamer.payment.infrastructure.excel.validators.detail;

import com.kynsof.share.core.application.excel.validator.ExcelRuleValidator;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsoft.finamer.payment.domain.excel.bean.detail.PaymentDetailRow;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;
import java.util.Objects;

public class PaymentImportDetailAmountValidator extends ExcelRuleValidator<PaymentDetailRow> {
    protected PaymentImportDetailAmountValidator(ApplicationEventPublisher applicationEventPublisher) {
        super(applicationEventPublisher);
    }

    @Override
    public boolean validate(PaymentDetailRow obj, List<ErrorField> errorFieldList) {
        if (Objects.isNull(obj.getBalance())){
            errorFieldList.add(new ErrorField("Balance", "Payment Amount can't be empty"));
            return false;
        }
        boolean valid = obj.getBalance() > 0;
        if (!valid) {
            errorFieldList.add(new ErrorField("Balance", "Payment Amount must be greater than 0"));
            return false;
        }
        return true;
    }
}

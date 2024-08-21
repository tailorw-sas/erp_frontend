package com.kynsoft.finamer.payment.infrastructure.excel.validators.expense;

import com.kynsof.share.core.application.excel.validator.ExcelRuleValidator;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsoft.finamer.payment.domain.excel.bean.payment.PaymentExpenseRow;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;
import java.util.Objects;

public class PaymentImportAmountValidator extends ExcelRuleValidator<PaymentExpenseRow> {
    protected PaymentImportAmountValidator(ApplicationEventPublisher applicationEventPublisher) {
        super(applicationEventPublisher);
    }

    @Override
    public boolean validate(PaymentExpenseRow obj, List<ErrorField> errorFieldList) {
        if (Objects.isNull(obj.getAmount())){
            errorFieldList.add(new ErrorField("Payment Amount", "Payment Amount can't be empty"));
            return false;
        }
        boolean valid = obj.getAmount() > 0;
        if (!valid) {
            errorFieldList.add(new ErrorField("Payment Amount", "Payment Amount must be greater than 0"));
            return false;
        }
        return true;
    }
}

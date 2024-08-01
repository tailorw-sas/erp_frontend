package com.kynsoft.finamer.payment.infrastructure.excel.validators.bank;

import com.kynsof.share.core.application.excel.validator.ExcelRuleValidator;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsoft.finamer.payment.domain.excel.PaymentImportError;
import com.kynsoft.finamer.payment.domain.excel.bean.PaymentBankRow;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;
import java.util.Objects;

public class PaymentImportAmountValidator extends ExcelRuleValidator<PaymentBankRow> {
    protected PaymentImportAmountValidator(ApplicationEventPublisher applicationEventPublisher) {
        super(applicationEventPublisher);
    }

    @Override
    public boolean validate(PaymentBankRow obj, List<ErrorField> errorFieldList) {
        if (Objects.isNull(obj.getPaymentAmount())){
            errorFieldList.add(new ErrorField("Payment Amount", "Payment Amount can't be empty"));
            return false;
        }
        boolean valid = obj.getPaymentAmount() > 0;
        if (valid){
            return true;
        }else{
            errorFieldList.add(new ErrorField("Payment Amount","Payment amount must not be 0"));
            return false;
        }
    }
}

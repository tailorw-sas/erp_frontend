package com.kynsoft.finamer.payment.infrastructure.excel.validators.expense;

import com.kynsof.share.core.application.excel.validator.ExcelRuleValidator;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsoft.finamer.payment.domain.excel.PaymentImportError;
import com.kynsoft.finamer.payment.domain.excel.bean.PaymentRow;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;
import java.util.Objects;

public class PaymentImportAmountValidator extends ExcelRuleValidator<PaymentRow> {
    protected PaymentImportAmountValidator(ApplicationEventPublisher applicationEventPublisher) {
        super(applicationEventPublisher);
    }

    @Override
    public boolean validate(PaymentRow obj, List<ErrorField> errorFieldList) {
        if (Objects.isNull(obj.getPaymentExp())){
            errorFieldList.add(new ErrorField("Payment Exp", "Payment Exp can't be empty"));
            return false;
        }
      boolean valid = obj.getPaymentExp() != 0;
      if (valid){
          return true;
      }else{
          errorFieldList.add(new ErrorField("Payment Exp","The payment exp must not be 0"));
          return false;
      }
    }
}

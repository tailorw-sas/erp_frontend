package com.kynsoft.finamer.payment.infrastructure.excel.validators.detail;

import com.kynsof.share.core.application.excel.validator.ExcelRuleValidator;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsoft.finamer.payment.domain.dto.PaymentDto;
import com.kynsoft.finamer.payment.domain.excel.bean.detail.PaymentDetailRow;
import com.kynsoft.finamer.payment.domain.services.IPaymentService;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;
import java.util.Objects;

public class PaymentImportDetailAmountValidator extends ExcelRuleValidator<PaymentDetailRow> {

    private final IPaymentService service;
    protected PaymentImportDetailAmountValidator(ApplicationEventPublisher applicationEventPublisher, IPaymentService service) {
        super(applicationEventPublisher);
        this.service = service;
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
        try {
            PaymentDto paymentDto = this.service.findByPaymentId(Long.parseLong(obj.getPaymentId()));
            if (Objects.isNull(obj.getBalance())) {
                errorFieldList.add(new ErrorField("Balance", "Payment Amount can't be empty"));
                return false;
            }

            if (paymentDto.getPaymentAmount() < obj.getBalance()) {
                errorFieldList.add(new ErrorField("Balance", "The balance is greater than the amount of the payment."));
                //errorFieldList.add(new ErrorField("Balance", "El valor del payment es menor que el balance."));
                return false;
            }
        } catch (Exception e) {
            errorFieldList.add(new ErrorField("Payment Id", "The Payment not found."));
            return false;
        }
        return true;
    }
}

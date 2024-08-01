package com.kynsoft.finamer.payment.infrastructure.excel.validators.expense;

import com.kynsof.share.core.application.excel.validator.IValidatorFactory;
import com.kynsoft.finamer.payment.domain.excel.PaymentImportError;
import com.kynsoft.finamer.payment.domain.excel.bean.PaymentRow;
import com.kynsoft.finamer.payment.infrastructure.excel.event.PaymentImportErrorEvent;
import com.kynsoft.finamer.payment.infrastructure.excel.validators.CommonImportValidators;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class PaymentValidatorFactory extends IValidatorFactory<PaymentRow> {
    private PaymentImportAmountValidator paymentImportAgencyValidator;
    private final CommonImportValidators commonImportValidators;

    public PaymentValidatorFactory(CommonImportValidators commonImportValidators, ApplicationEventPublisher applicationEventPublisher) {
        super(applicationEventPublisher);
        this.commonImportValidators = commonImportValidators;
    }


    @Override
    public void createValidators() {
        paymentImportAgencyValidator = new PaymentImportAmountValidator(applicationEventPublisher);
    }

    @Override
    public boolean validate(PaymentRow toValidate) {
        commonImportValidators.validateAgency(toValidate.getAgencyCode(), errorFieldList);
        commonImportValidators.validateHotel(toValidate.getHotelCode(), errorFieldList);
        commonImportValidators.validateRemarks(toValidate.getRemarks(), errorFieldList);
        commonImportValidators.validateTransactionDate(toValidate.getTransactionDate(), "dd/MM/yyyy",errorFieldList);
        commonImportValidators.validateCloseOperation(toValidate.getTransactionDate(), toValidate.getHotelCode(),"dd/MM/yyyy", errorFieldList);
        paymentImportAgencyValidator.validate(toValidate, errorFieldList);
        if (this.hasErrors()) {
            PaymentImportErrorEvent paymentImportErrorEvent = new PaymentImportErrorEvent(new PaymentImportError(null, toValidate.getImportProcessId(), errorFieldList, toValidate));
            this.sendErrorEvent(paymentImportErrorEvent);
        }
        boolean result = !this.hasErrors();
        this.clearErrors();
        return result;
    }
}

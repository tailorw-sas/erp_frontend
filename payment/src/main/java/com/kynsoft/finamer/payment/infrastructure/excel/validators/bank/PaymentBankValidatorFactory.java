package com.kynsoft.finamer.payment.infrastructure.excel.validators.bank;

import com.kynsof.share.core.application.excel.validator.IValidatorFactory;
import com.kynsoft.finamer.payment.domain.excel.PaymentImportError;
import com.kynsoft.finamer.payment.domain.excel.bean.PaymentBankRow;
import com.kynsoft.finamer.payment.domain.services.IManageBankAccountService;
import com.kynsoft.finamer.payment.infrastructure.excel.event.PaymentImportErrorEvent;
import com.kynsoft.finamer.payment.infrastructure.excel.validators.CommonImportValidators;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class PaymentBankValidatorFactory extends IValidatorFactory<PaymentBankRow> {

    private final ApplicationEventPublisher paymentEventPublisher;
    private final CommonImportValidators commonImportValidators;

    private  PaymentImportAmountValidator paymentImportAmountValidator;
    private  PaymentImportBankAccountValidator paymentImportBankAccountValidator;

    private final IManageBankAccountService bankAccountService;

    public PaymentBankValidatorFactory(ApplicationEventPublisher paymentEventPublisher,
                                       CommonImportValidators commonImportValidators, IManageBankAccountService bankAccountService) {
        super(paymentEventPublisher);
        this.paymentEventPublisher = paymentEventPublisher;
        this.commonImportValidators = commonImportValidators;
        this.bankAccountService = bankAccountService;
    }

    @Override
    public void createValidators() {
        paymentImportAmountValidator =new PaymentImportAmountValidator(paymentEventPublisher);
      paymentImportBankAccountValidator = new PaymentImportBankAccountValidator(applicationEventPublisher,bankAccountService);
    }

    @Override
    public boolean validate(PaymentBankRow toValidate) {
                commonImportValidators.validateAgency(toValidate.getAgencyCode(),errorFieldList);
                commonImportValidators.validateHotel(toValidate.getHotelCode(),errorFieldList);
                commonImportValidators.validateRemarks(toValidate.getRemarks(),errorFieldList);
                commonImportValidators.validateTransactionDate(toValidate.getTransactionDate(),"dd/MM/yyyy",errorFieldList) ;
                commonImportValidators.validateCloseOperation(toValidate.getTransactionDate(),toValidate.getHotelCode(),"dd/MM/yyyy",errorFieldList) ;
                paymentImportAmountValidator.validate(toValidate,errorFieldList);
                paymentImportBankAccountValidator.validate(toValidate,errorFieldList);
        if (this.hasErrors()) {
            PaymentImportErrorEvent paymentImportErrorEvent = new PaymentImportErrorEvent(new PaymentImportError(null, toValidate.getImportProcessId(), errorFieldList, toValidate));
            this.sendErrorEvent(paymentImportErrorEvent);
        }
        boolean result = !this.hasErrors();
        this.clearErrors();
        return result;
    }
}

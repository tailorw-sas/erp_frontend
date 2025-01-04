package com.kynsoft.finamer.payment.infrastructure.excel.validators.expense;

import com.kynsof.share.core.application.excel.validator.IValidatorFactory;
import com.kynsoft.finamer.payment.domain.dto.ManageAgencyDto;
import com.kynsoft.finamer.payment.domain.dto.ManageClientDto;
import com.kynsoft.finamer.payment.domain.excel.bean.payment.PaymentExpenseRow;
import com.kynsoft.finamer.payment.domain.excel.error.PaymentExpenseRowError;
import com.kynsoft.finamer.payment.domain.services.IManageAgencyService;
import com.kynsoft.finamer.payment.infrastructure.excel.event.error.expense.PaymentImportExpenseErrorEvent;
import com.kynsoft.finamer.payment.infrastructure.excel.validators.CommonImportValidators;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class PaymentValidatorFactory extends IValidatorFactory<PaymentExpenseRow> {
    private PaymentImportAmountValidator paymentImportAgencyValidator;
    private final CommonImportValidators commonImportValidators;
    private final IManageAgencyService manageAgencyService;

    public PaymentValidatorFactory(CommonImportValidators commonImportValidators, ApplicationEventPublisher applicationEventPublisher,
                                   IManageAgencyService manageAgencyService) {
        super(applicationEventPublisher);
        this.commonImportValidators = commonImportValidators;
        this.manageAgencyService = manageAgencyService;
    }


    @Override
    public void createValidators() {
        paymentImportAgencyValidator = new PaymentImportAmountValidator(applicationEventPublisher);
    }

    @Override
    public boolean validate(PaymentExpenseRow toValidate) {
        boolean isAgencyValid =commonImportValidators.validateAgency(toValidate.getManageAgencyCode(), errorFieldList);
        commonImportValidators.validateHotel(toValidate.getManageHotelCode(), errorFieldList);
        commonImportValidators.validateRemarks(toValidate.getRemarks(), errorFieldList);
        boolean isValidTransactionDate = commonImportValidators.validateTransactionDate(toValidate.getTransactionDate(),
                "dd/MM/yyyy", errorFieldList);
        commonImportValidators.validateCloseOperation(toValidate.getTransactionDate(), toValidate.getManageHotelCode(),
                "dd/MM/yyyy", errorFieldList, isValidTransactionDate);
        paymentImportAgencyValidator.validate(toValidate, errorFieldList);
        if (this.hasErrors()) {
            if (isAgencyValid){
                ManageAgencyDto agencyDto =manageAgencyService.findByCode(toValidate.getManageAgencyCode());
                ManageClientDto clientDto = agencyDto.getClient();
                if (Objects.nonNull(clientDto))
                    toValidate.setManageClientCode(clientDto.getName());
            }
            PaymentImportExpenseErrorEvent paymentImportErrorEvent =
                    new PaymentImportExpenseErrorEvent(new PaymentExpenseRowError(null, toValidate.getRowNumber(),
                            toValidate.getImportProcessId(), errorFieldList, toValidate));
            this.sendErrorEvent(paymentImportErrorEvent);
        }
        boolean result = !this.hasErrors();
        this.clearErrors();
        return result;
    }
}

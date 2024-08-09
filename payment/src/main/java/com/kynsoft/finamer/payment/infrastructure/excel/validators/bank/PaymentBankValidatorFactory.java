package com.kynsoft.finamer.payment.infrastructure.excel.validators.bank;

import com.kynsof.share.core.application.excel.validator.IValidatorFactory;
import com.kynsoft.finamer.payment.domain.dto.ManageAgencyDto;
import com.kynsoft.finamer.payment.domain.dto.ManageClientDto;
import com.kynsoft.finamer.payment.domain.excel.error.PaymentBankRowError;
import com.kynsoft.finamer.payment.domain.excel.bean.payment.PaymentBankRow;
import com.kynsoft.finamer.payment.domain.services.IManageAgencyService;
import com.kynsoft.finamer.payment.domain.services.IManageBankAccountService;
import com.kynsoft.finamer.payment.domain.services.IManageHotelService;
import com.kynsoft.finamer.payment.infrastructure.excel.event.error.bank.PaymentImportBankAccountErrorEvent;
import com.kynsoft.finamer.payment.infrastructure.excel.validators.CommonImportValidators;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class PaymentBankValidatorFactory extends IValidatorFactory<PaymentBankRow> {

    private final ApplicationEventPublisher paymentEventPublisher;
    private final CommonImportValidators commonImportValidators;

    private  PaymentImportAmountValidator paymentImportAmountValidator;
    private  PaymentImportBankAccountValidator paymentImportBankAccountValidator;

    private final IManageBankAccountService bankAccountService;

    private final IManageHotelService manageHotelService;
    private final IManageAgencyService agencyService;

    public PaymentBankValidatorFactory(ApplicationEventPublisher paymentEventPublisher,
                                       CommonImportValidators commonImportValidators,
                                       IManageBankAccountService bankAccountService,
                                       IManageHotelService manageHotelService,
                                       IManageAgencyService agencyService) {
        super(paymentEventPublisher);
        this.paymentEventPublisher = paymentEventPublisher;
        this.commonImportValidators = commonImportValidators;
        this.bankAccountService = bankAccountService;
        this.manageHotelService = manageHotelService;
        this.agencyService = agencyService;
    }

    @Override
    public void createValidators() {
        paymentImportAmountValidator =new PaymentImportAmountValidator(paymentEventPublisher);
      paymentImportBankAccountValidator = new PaymentImportBankAccountValidator(applicationEventPublisher,
              bankAccountService,manageHotelService);
    }

    @Override
    public boolean validate(PaymentBankRow toValidate) {
               boolean isAgencyValid= commonImportValidators.validateAgency(toValidate.getManageAgencyCode(),errorFieldList);
                commonImportValidators.validateHotel(toValidate.getManageHotelCode(),errorFieldList);
                commonImportValidators.validateRemarks(toValidate.getRemarks(),errorFieldList);
                boolean isValidTransactionDate=commonImportValidators.validateTransactionDate(toValidate.getTransactionDate(),
                        "dd/MM/yyyy", errorFieldList) ;
                commonImportValidators.validateCloseOperation(toValidate.getTransactionDate(),
                        toValidate.getManageHotelCode(),"dd/MM/yyyy",errorFieldList,isValidTransactionDate) ;
                paymentImportAmountValidator.validate(toValidate,errorFieldList);
                paymentImportBankAccountValidator.validate(toValidate,errorFieldList);
        if (this.hasErrors()) {
            if (isAgencyValid){
                ManageAgencyDto agencyDto =agencyService.findByCode(toValidate.getManageAgencyCode());
                ManageClientDto clientDto = agencyDto.getClient();
                if (Objects.nonNull(clientDto))
                    toValidate.setManageClientCode(clientDto.getName());
            }
            PaymentImportBankAccountErrorEvent paymentImportErrorEvent = new PaymentImportBankAccountErrorEvent(
                    new PaymentBankRowError(null,toValidate.getRowNumber(), toValidate.getImportProcessId(),
                            errorFieldList, toValidate));
            this.sendErrorEvent(paymentImportErrorEvent);
        }
        boolean result = !this.hasErrors();
        this.clearErrors();
        return result;
    }
}

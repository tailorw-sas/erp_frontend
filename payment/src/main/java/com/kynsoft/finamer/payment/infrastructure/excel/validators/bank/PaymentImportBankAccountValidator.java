package com.kynsoft.finamer.payment.infrastructure.excel.validators.bank;

import com.kynsof.share.core.application.excel.validator.ExcelRuleValidator;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsoft.finamer.payment.domain.excel.bean.PaymentBankRow;
import com.kynsoft.finamer.payment.domain.services.IManageBankAccountService;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;
import java.util.Objects;

public class PaymentImportBankAccountValidator extends ExcelRuleValidator<PaymentBankRow> {

    private final IManageBankAccountService bankAccountService;

    protected PaymentImportBankAccountValidator(ApplicationEventPublisher applicationEventPublisher,
                                                IManageBankAccountService bankAccountService) {
        super(applicationEventPublisher);
        this.bankAccountService = bankAccountService;
    }

    @Override
    public boolean validate(PaymentBankRow obj, List<ErrorField> errorFieldList) {
        if (Objects.isNull(obj.getBankAccount())) {
            errorFieldList.add(new ErrorField("Bank Account", "Bank Account can't be empty"));
            return false;
        }
        boolean result = bankAccountService.existByAccountNumber(obj.getBankAccount());
        if (!result) {
            errorFieldList.add(new ErrorField("Bank Account", "The bank account not exist "));
        }

        return result;
    }

}

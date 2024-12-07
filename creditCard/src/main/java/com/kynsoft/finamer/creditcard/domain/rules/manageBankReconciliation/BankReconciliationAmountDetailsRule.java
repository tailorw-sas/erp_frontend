package com.kynsoft.finamer.creditcard.domain.rules.manageBankReconciliation;

import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.rules.BusinessRule;
import com.kynsof.share.utils.BankerRounding;
import com.kynsoft.finamer.creditcard.domain.dto.ParameterizationDto;
import com.kynsoft.finamer.creditcard.domain.services.IParameterizationService;

public class BankReconciliationAmountDetailsRule extends BusinessRule {

    private final Double amount;

    private final Double detailsAmount;

    private final IParameterizationService parameterizationService;

    public BankReconciliationAmountDetailsRule(Double amount, Double detailsAmount, IParameterizationService parameterizationService) {
        super(
                DomainErrorMessage.MANAGE_BANK_RECONCILIATION_AMOUNT_DETAILS,
                new ErrorField("amount", DomainErrorMessage.MANAGE_BANK_RECONCILIATION_AMOUNT_DETAILS.getReasonPhrase()));
        this.amount = amount;
        this.detailsAmount = detailsAmount;
        this.parameterizationService = parameterizationService;
    }

    @Override
    public boolean isBroken() {
        ParameterizationDto parameterizationDto = this.parameterizationService.findActiveParameterization();
        int decimals = parameterizationDto != null ? parameterizationDto.getDecimals() : 2;
        return BankerRounding.round(amount, decimals) < BankerRounding.round(detailsAmount, decimals);
    }
}

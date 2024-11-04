package com.kynsoft.finamer.creditcard.domain.rules.manageTransactionStatus;

import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.rules.BusinessRule;

public class ManageTransactionStatusNameMustBeNullRule extends BusinessRule {

    private final String name;

    public ManageTransactionStatusNameMustBeNullRule(String name) {
        super(
                DomainErrorMessage.MANAGER_TRANSACTION_STATUS_NAME_CANNOT_BE_EMPTY, 
                new ErrorField("name", "The name cannot be empty.")
        );
        this.name = name;
    }

    @Override
    public boolean isBroken() {
        return this.name == null || this.name.isEmpty();
    }

}

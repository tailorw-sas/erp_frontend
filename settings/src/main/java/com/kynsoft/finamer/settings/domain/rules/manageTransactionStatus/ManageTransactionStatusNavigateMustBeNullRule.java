package com.kynsoft.finamer.settings.domain.rules.manageTransactionStatus;

import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.rules.BusinessRule;
import com.kynsoft.finamer.settings.domain.dtoEnum.NavigateTransactionStatus;
import java.util.Set;

public class ManageTransactionStatusNavigateMustBeNullRule extends BusinessRule {

    private final Set<NavigateTransactionStatus> navigate;

    public ManageTransactionStatusNavigateMustBeNullRule(Set<NavigateTransactionStatus> navigate) {
        super(
                DomainErrorMessage.MANAGER_TRANSACTION_STATUS_NAVIGATE_CANNOT_BE_EMPTY, 
                new ErrorField("name", "The navigate cannot be empty.")
        );
        this.navigate = navigate;
    }

    @Override
    public boolean isBroken() {
        return this.navigate == null || this.navigate.isEmpty();
    }

}

package com.kynsoft.finamer.settings.domain.rules.manageReport;

import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.rules.BusinessRule;

public class ManageReportNameMustBeNullRule extends BusinessRule {

    private final String name;

    public ManageReportNameMustBeNullRule(String name) {
        super(
                DomainErrorMessage.NAME_CANNOT_BE_EMPTY,
                new ErrorField("name", DomainErrorMessage.NAME_CANNOT_BE_EMPTY.getReasonPhrase())
        );
        this.name = name;
    }

    @Override
    public boolean isBroken() {
        return this.name == null || this.name.isEmpty();
    }

}

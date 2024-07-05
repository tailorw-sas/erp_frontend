package com.kynsoft.report.domain.rules.jasperReport;

import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.rules.BusinessRule;

public class ManageReportNameMustBeNullRule extends BusinessRule {

    private final String name;

    public ManageReportNameMustBeNullRule(String name) {
        super(
                DomainErrorMessage.FIELD_IS_REQUIRED, 
                new ErrorField("name", "The field is required.")
        );
        this.name = name;
    }

    @Override
    public boolean isBroken() {
        return this.name == null || this.name.isEmpty();
    }

}

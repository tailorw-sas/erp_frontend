package com.kynsoft.report.domain.rules.jasperReport;

import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.rules.BusinessRule;

public class ManageReportParentIndexMustBeNullRule extends BusinessRule {

    private final Double parentIndex;

    public ManageReportParentIndexMustBeNullRule(Double parentIndex) {
        super(
                DomainErrorMessage.FIELD_IS_REQUIRED, 
                new ErrorField("parentIndex", "The field is required.")
        );
        this.parentIndex = parentIndex;
    }

    @Override
    public boolean isBroken() {
        return this.parentIndex == null;
    }

}

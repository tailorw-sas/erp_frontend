package com.kynsoft.report.domain.rules.jasperReport;

import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.rules.BusinessRule;

public class ManageReportCodeMustBeNullRule extends BusinessRule {

    private final String code;

    public ManageReportCodeMustBeNullRule(String code) {
        super(
                DomainErrorMessage.FIELD_IS_REQUIRED, 
                new ErrorField("code", "The field is required.")
        );
        this.code = code;
    }

    @Override
    public boolean isBroken() {
        return this.code == null || this.code.isEmpty();
    }

}

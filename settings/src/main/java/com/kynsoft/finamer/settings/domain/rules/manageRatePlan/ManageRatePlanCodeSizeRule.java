package com.kynsoft.finamer.settings.domain.rules.manageRatePlan;

import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.rules.BusinessRule;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ManageRatePlanCodeSizeRule extends BusinessRule {

    private final String code;

    public ManageRatePlanCodeSizeRule(String code) {
        super(
                DomainErrorMessage.MANAGE_RATE_PLAN_CODE_SIZE, 
                new ErrorField("code", "The code is not accepted.")
        );
        this.code = code;
    }

    @Override
    public boolean isBroken() {
        return !validateCode(code);
    }

    private boolean validateCode(String code) {
        Pattern pattern = Pattern.compile("^[a-zA-Z]{1,10}$");
        Matcher matcher = pattern.matcher(code);

        return matcher.matches();
    }
}

package com.kynsoft.finamer.settings.domain.rules.manageEmployeeGroup;

import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.rules.BusinessRule;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ManageEmployeeGroupCodeSizeRule extends BusinessRule{

    private final String code;

    public ManageEmployeeGroupCodeSizeRule(String code) {
        super(
                DomainErrorMessage.MANAGE_EMPLOYEE_GROUP_CODE_SIZE,
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

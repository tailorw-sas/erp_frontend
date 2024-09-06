package com.kynsoft.finamer.settings.domain.rules.manageCityState;

import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.rules.BusinessRule;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ManageCityStateCodeSizeRule extends BusinessRule {

    private final String code;

    public ManageCityStateCodeSizeRule(String code) {
        super(
                DomainErrorMessage.MANAGER_CITY_STATE_CODE_SIZE,
                new ErrorField("code", "The code is not accepted.")
        );
        this.code = code;
    }

    @Override
    public boolean isBroken() {
        return !validateCode(code);
    }

    private boolean validateCode(String code) {
        Pattern pattern = Pattern.compile("^[a-zA-Z0-9]{3,5}$");
        Matcher matcher = pattern.matcher(code);

        return matcher.matches();
    }
}

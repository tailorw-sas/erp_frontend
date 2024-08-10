package com.kynsoft.finamer.settings.domain.rules.managerCountry;

import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.rules.BusinessRule;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ManagerCountryCodeSizeRule extends BusinessRule {

    private final String code;

    public ManagerCountryCodeSizeRule(String code) {
        super(
                DomainErrorMessage.MANAGER_COUNTRY_CODE_SIZE, 
                new ErrorField("code", "The manager country code is not accepted.")
        );
        this.code = code;
    }

    @Override
    public boolean isBroken() {
        return !validateCode(code);
    }

    private boolean validateCode(String code) {
        Pattern pattern = Pattern.compile("^[a-zA-Z]{2,20}$");
        Matcher matcher = pattern.matcher(code);

        return matcher.matches();
    }
}

package com.kynsoft.finamer.invoicing.domain.rules.managerCountry;

import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.rules.BusinessRule;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ManagerCountryDialCodeSizeRule extends BusinessRule {

    private final String dialCode;

    public ManagerCountryDialCodeSizeRule(String dialCode) {
        super(
                DomainErrorMessage.MANAGER_COUNTRY_DIAL_CODE_SIZE, 
                new ErrorField("code", "The manager country dial code is not accepted.")
        );
        this.dialCode = dialCode;
    }

    @Override
    public boolean isBroken() {
        return !validateDialCode(dialCode);
    }

    private boolean validateDialCode(String dialCode) {
        Pattern pattern = Pattern.compile("^(\\+[1-9]\\d{0,4})$");
        Matcher matcher = pattern.matcher(dialCode);

        return matcher.matches();
    }
}

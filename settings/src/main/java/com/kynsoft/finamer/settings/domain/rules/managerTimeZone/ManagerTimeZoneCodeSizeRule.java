package com.kynsoft.finamer.settings.domain.rules.managerTimeZone;

import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.rules.BusinessRule;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ManagerTimeZoneCodeSizeRule extends BusinessRule {

    private final String code;

    public ManagerTimeZoneCodeSizeRule(String code) {
        super(
                DomainErrorMessage.MANAGER_TIME_ZONE_CODE_SIZE, 
                new ErrorField("code", "The manager time zone code is not accepted.")
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

package com.kynsoft.finamer.settings.domain.rules.managerB2BPartner;

import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.rules.BusinessRule;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ManagerB2BPartnerCodeSizeRule extends BusinessRule {

    private final String code;

    public ManagerB2BPartnerCodeSizeRule(String code) {
        super(
                DomainErrorMessage.MANAGER_B2BPARTNER_CODE_SIZE, 
                new ErrorField("code", "The manager B2B Partner code is not accepted.")
        );
        this.code = code;
    }

    @Override
    public boolean isBroken() {
        return !validateCode(code);
    }

    private boolean validateCode(String code) {
        Pattern pattern = Pattern.compile("^[a-zA-Z]{3,5}$");
        Matcher matcher = pattern.matcher(code);

        return matcher.matches();
    }
}

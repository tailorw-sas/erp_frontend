package com.kynsoft.finamer.settings.domain.rules.manageEmployee;

import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.rules.BusinessRule;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ManageEmployeePhoneExtensionRule extends BusinessRule {

    private final String phoneExtension;

    public ManageEmployeePhoneExtensionRule(String phoneExtension) {
        super(
                DomainErrorMessage.PHONE_EXTENSION,
                new ErrorField("phoneExtension", DomainErrorMessage.PHONE_EXTENSION.getReasonPhrase())
        );
        this.phoneExtension = phoneExtension;
    }

    @Override
    public boolean isBroken() {
        return !validateCode(phoneExtension);
    }

    private boolean validateCode(String phoneExtension) {
        Pattern pattern = Pattern.compile("^[0-9]{1,10}$");
        Matcher matcher = pattern.matcher(phoneExtension);

        return matcher.matches();
    }
}

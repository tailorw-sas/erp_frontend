package com.kynsoft.finamer.settings.domain.rules.manageContact;

import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.rules.BusinessRule;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ManageContactEmailRule extends BusinessRule {

    private final String email;

    public ManageContactEmailRule(String email) {
        super(
                DomainErrorMessage.MANAGE_CONTACT_EMAIL_INVALID,
                new ErrorField("email", "The email is not accepted.")
        );
        this.email = email;
    }

    @Override
    public boolean isBroken() {
        return !validateCode(email);
    }

    private boolean validateCode(String email) {
        Pattern pattern = Pattern.compile("^[\\w.-]+@[\\w.-]+\\.[A-Za-z]{2,6}$");
        Matcher matcher = pattern.matcher(email);

        return matcher.matches();
    }
}

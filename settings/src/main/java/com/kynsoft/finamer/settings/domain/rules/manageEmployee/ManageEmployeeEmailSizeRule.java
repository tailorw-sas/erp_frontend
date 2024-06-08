package com.kynsoft.finamer.settings.domain.rules.manageEmployee;

import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.rules.BusinessRule;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ManageEmployeeEmailSizeRule extends BusinessRule {

    private final String email;

    public ManageEmployeeEmailSizeRule(String email) {
        super(
                DomainErrorMessage.MANAGE_EMPLOYEE_EMAIL_SIZE, 
                new ErrorField("email", "The email is not accepted.")
        );
        this.email = email;
    }

    @Override
    public boolean isBroken() {
        return !validateCode(email);
    }

    private boolean validateCode(String email) {
        Pattern pattern = Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");
        Matcher matcher = pattern.matcher(email);

        return matcher.matches();
    }
}

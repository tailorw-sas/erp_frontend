package com.kynsoft.finamer.settings.domain.rules.manageBankAccount;

import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.rules.BusinessRule;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ManageBankAccountNumberRule extends BusinessRule {

    private final String accountNumber;

    public ManageBankAccountNumberRule(String accountNumber) {
        super(
                DomainErrorMessage.ACCOUNT_NUMBER,
                new ErrorField("accountNumber", DomainErrorMessage.ACCOUNT_NUMBER.getReasonPhrase())
        );
        this.accountNumber = accountNumber;
    }

    @Override
    public boolean isBroken() {
        return !validateCode(accountNumber);
    }

    private boolean validateCode(String accountNumber) {
        Pattern pattern = Pattern.compile("^[0-9]{1,20}$");
        Matcher matcher = pattern.matcher(accountNumber);

        return matcher.matches();
    }
}

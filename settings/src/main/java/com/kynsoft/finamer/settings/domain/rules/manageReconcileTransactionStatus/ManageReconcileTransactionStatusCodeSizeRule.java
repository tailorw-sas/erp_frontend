package com.kynsoft.finamer.settings.domain.rules.manageReconcileTransactionStatus;

import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.rules.BusinessRule;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ManageReconcileTransactionStatusCodeSizeRule extends BusinessRule{

    private final String code;

    public ManageReconcileTransactionStatusCodeSizeRule(String code) {
        super(
                DomainErrorMessage.MANAGE_PAYMENT_SOURCE_CODE_SIZE,
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

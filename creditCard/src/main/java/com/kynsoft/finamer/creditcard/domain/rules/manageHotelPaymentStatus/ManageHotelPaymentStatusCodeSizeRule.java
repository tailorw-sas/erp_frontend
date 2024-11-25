package com.kynsoft.finamer.creditcard.domain.rules.manageHotelPaymentStatus;

import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.rules.BusinessRule;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ManageHotelPaymentStatusCodeSizeRule extends BusinessRule{

    private final String code;

    public ManageHotelPaymentStatusCodeSizeRule(String code) {
        super(
                DomainErrorMessage.CODE_SIZE,
                new ErrorField("code", DomainErrorMessage.CODE_SIZE.getReasonPhrase())
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

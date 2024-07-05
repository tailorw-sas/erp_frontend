package com.kynsoft.finamer.payment.domain.rules.paymentDetail;

import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.rules.BusinessRule;

public class CheckMinNumberOfCharacterInRemarkRule extends BusinessRule {

    private final Integer minNumberOfCharacter;
    private final String remark;

    public CheckMinNumberOfCharacterInRemarkRule(Integer minNumberOfCharacter, String remark) {
        super(DomainErrorMessage.CHECK_MINIMUM_CHARACTER_REQUIREMENT, new ErrorField("remark", DomainErrorMessage.CHECK_MINIMUM_CHARACTER_REQUIREMENT.getReasonPhrase()));
        this.minNumberOfCharacter = minNumberOfCharacter;
        this.remark = remark;
    }

    /**
     * Remark: Sirve para poner alguna description y debe cumplir con campo Min Number of character
     * @return true or false
     */
    @Override
    public boolean isBroken() {
        return this.minNumberOfCharacter < this.remark.length();
    }

}

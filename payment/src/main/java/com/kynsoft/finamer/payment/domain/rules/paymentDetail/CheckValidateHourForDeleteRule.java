package com.kynsoft.finamer.payment.domain.rules.paymentDetail;

import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.rules.BusinessRule;
import java.time.Duration;
import java.time.LocalDateTime;

public class CheckValidateHourForDeleteRule extends BusinessRule {

    private final LocalDateTime createdAt;
    private final LocalDateTime now;

    public CheckValidateHourForDeleteRule(LocalDateTime createdAt) {
        super(DomainErrorMessage.CHECK_PAYMENT_CREATEAT_VALIDATE_HOUR_FOR_DELETE, new ErrorField("createdAt", DomainErrorMessage.CHECK_PAYMENT_CREATEAT_VALIDATE_HOUR_FOR_DELETE.getReasonPhrase()));
        this.now = LocalDateTime.now();
        this.createdAt = createdAt;
    }

    @Override
    public boolean isBroken() {
        Duration duration = Duration.between(this.createdAt, this.now);
        return duration.compareTo(Duration.ofHours(24)) >= 0;
    }

}

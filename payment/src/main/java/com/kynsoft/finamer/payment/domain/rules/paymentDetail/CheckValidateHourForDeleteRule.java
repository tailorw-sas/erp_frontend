package com.kynsoft.finamer.payment.domain.rules.paymentDetail;

import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.rules.BusinessRule;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneId;

public class CheckValidateHourForDeleteRule extends BusinessRule {

    private final OffsetDateTime createdAt;
    private final OffsetDateTime now;

    public CheckValidateHourForDeleteRule(OffsetDateTime createdAt) {
        super(DomainErrorMessage.CHECK_PAYMENT_CREATEAT_VALIDATE_HOUR_FOR_DELETE, new ErrorField("createdAt", DomainErrorMessage.CHECK_PAYMENT_CREATEAT_VALIDATE_HOUR_FOR_DELETE.getReasonPhrase()));
        this.now = OffsetDateTime.now(ZoneId.of("UTC"));
        this.createdAt = createdAt;
    }

    @Override
    public boolean isBroken() {
        Duration duration = Duration.between(this.createdAt, this.now);
        return duration.compareTo(Duration.ofHours(24)) >= 0;
    }

}

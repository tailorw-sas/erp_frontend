package com.kynsoft.finamer.payment.application.query.payment.countByAgency;

import com.kynsof.share.core.domain.bus.query.IResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class CountAgencyByPaymentBalanceAndDepositBalanceResponse implements IResponse {

    private boolean inActive;

    public CountAgencyByPaymentBalanceAndDepositBalanceResponse(boolean inActive) {
        this.inActive = inActive;
    }
}

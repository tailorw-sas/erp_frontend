package com.kynsoft.finamer.payment.application.query.payment.countByAgency;

import com.kynsof.share.core.domain.bus.query.IQuery;
import lombok.Getter;

import java.util.UUID;

@Getter
public class CountAgencyByPaymentBalanceAndDepositBalanceQuery  implements IQuery {

    private final UUID agencyId;

    public CountAgencyByPaymentBalanceAndDepositBalanceQuery(UUID agencyId) {
        this.agencyId = agencyId;
    }

}

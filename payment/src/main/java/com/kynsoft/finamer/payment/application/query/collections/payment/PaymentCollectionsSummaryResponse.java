package com.kynsoft.finamer.payment.application.query.collections.payment;

import com.kynsof.share.core.domain.bus.query.IResponse;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class PaymentCollectionsSummaryResponse implements IResponse {

    private int totalEntries;
    private double totalDeposit;
    private double totalDepositPercentage;
    private double totalAmount;
    private double totalApplied;
    private double totalAppliedPercentage;
    private double totalNotApplied;
    private double totalNotAppliedPercentage;

}

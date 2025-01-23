package com.kynsoft.finamer.payment.application.query.managePaymentTransactionType.getByCode;

import com.kynsof.share.core.domain.bus.query.IQuery;
import lombok.Getter;

@Getter
public class FindManagePaymentTransactionTypeByCodeQuery  implements IQuery {

    private final String code;

    public FindManagePaymentTransactionTypeByCodeQuery(String code) {
        this.code = code;
    }

}

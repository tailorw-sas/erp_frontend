package com.kynsoft.finamer.payment.application.query.http.setting.paymenteTransactionType;

import com.kynsof.share.core.domain.bus.query.IQuery;
import lombok.Getter;

import java.util.UUID;

@Getter
public class ManagePaymentTransactionTypeRequest  implements IQuery {

    private final UUID id;

    public ManagePaymentTransactionTypeRequest(UUID id) {
        this.id = id;
    }

}

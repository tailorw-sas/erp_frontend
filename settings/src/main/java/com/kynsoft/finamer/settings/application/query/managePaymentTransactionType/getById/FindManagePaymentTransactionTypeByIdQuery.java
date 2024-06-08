package com.kynsoft.finamer.settings.application.query.managePaymentTransactionType.getById;

import com.kynsof.share.core.domain.bus.query.IQuery;
import lombok.Getter;

import java.util.UUID;

@Getter
public class FindManagePaymentTransactionTypeByIdQuery  implements IQuery {

    private final UUID id;

    public FindManagePaymentTransactionTypeByIdQuery(UUID id) {
        this.id = id;
    }

}

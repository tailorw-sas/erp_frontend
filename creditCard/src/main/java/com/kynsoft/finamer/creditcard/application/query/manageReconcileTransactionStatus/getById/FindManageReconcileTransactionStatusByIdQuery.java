package com.kynsoft.finamer.creditcard.application.query.manageReconcileTransactionStatus.getById;

import com.kynsof.share.core.domain.bus.query.IQuery;
import lombok.Getter;

import java.util.UUID;

@Getter
public class FindManageReconcileTransactionStatusByIdQuery  implements IQuery {

    private final UUID id;

    public FindManageReconcileTransactionStatusByIdQuery(UUID id) {
        this.id = id;
    }

}

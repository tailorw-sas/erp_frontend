package com.kynsoft.finamer.settings.application.query.managePaymentTransactionStatus.getById;

import com.kynsof.share.core.domain.bus.query.IQuery;
import lombok.Getter;

import java.util.UUID;

@Getter
public class FindManagePaymentTransactionStatusByIdQuery  implements IQuery {

    private final UUID id;

    public FindManagePaymentTransactionStatusByIdQuery(UUID id) {
        this.id = id;
    }

}

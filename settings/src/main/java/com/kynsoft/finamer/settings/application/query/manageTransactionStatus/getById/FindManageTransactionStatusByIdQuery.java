package com.kynsoft.finamer.settings.application.query.manageTransactionStatus.getById;

import com.kynsof.share.core.domain.bus.query.IQuery;
import lombok.Getter;

import java.util.UUID;

@Getter
public class FindManageTransactionStatusByIdQuery  implements IQuery {

    private final UUID id;

    public FindManageTransactionStatusByIdQuery(UUID id) {
        this.id = id;
    }

}

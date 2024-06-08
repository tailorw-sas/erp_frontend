package com.kynsoft.finamer.settings.application.query.manageVCCTransactionType.getById;

import com.kynsof.share.core.domain.bus.query.IQuery;
import lombok.Getter;

import java.util.UUID;

@Getter
public class FindManageVCCTransactionTypeByIdQuery  implements IQuery {

    private final UUID id;

    public FindManageVCCTransactionTypeByIdQuery(UUID id) {
        this.id = id;
    }

}

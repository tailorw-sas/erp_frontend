package com.kynsoft.finamer.creditcard.application.query.managerBank.getById;

import com.kynsof.share.core.domain.bus.query.IQuery;
import lombok.Getter;

import java.util.UUID;

@Getter
public class FindManagerBankByIdQuery  implements IQuery {

    private final UUID id;

    public FindManagerBankByIdQuery(UUID id) {
        this.id = id;
    }

}

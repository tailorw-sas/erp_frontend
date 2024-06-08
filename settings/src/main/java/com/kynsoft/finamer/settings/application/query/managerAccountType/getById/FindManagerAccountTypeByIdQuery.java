package com.kynsoft.finamer.settings.application.query.managerAccountType.getById;

import com.kynsof.share.core.domain.bus.query.IQuery;
import lombok.Getter;

import java.util.UUID;

@Getter
public class FindManagerAccountTypeByIdQuery  implements IQuery {

    private final UUID id;

    public FindManagerAccountTypeByIdQuery(UUID id) {
        this.id = id;
    }

}

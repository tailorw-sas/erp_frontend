package com.kynsoft.finamer.settings.application.query.managerCountry.getById;

import com.kynsof.share.core.domain.bus.query.IQuery;
import lombok.Getter;

import java.util.UUID;

@Getter
public class FindManagerContryByIdQuery  implements IQuery {

    private final UUID id;

    public FindManagerContryByIdQuery(UUID id) {
        this.id = id;
    }

}

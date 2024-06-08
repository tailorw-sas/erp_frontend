package com.kynsoft.finamer.invoicing.application.query.getById;

import com.kynsof.share.core.domain.bus.query.IQuery;
import lombok.Getter;

import java.util.UUID;

@Getter
public class FindTestByIdQuery  implements IQuery {

    private final UUID id;

    public FindTestByIdQuery(UUID id) {
        this.id = id;
    }

}

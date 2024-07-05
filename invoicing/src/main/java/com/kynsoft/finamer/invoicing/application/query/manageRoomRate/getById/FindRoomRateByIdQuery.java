package com.kynsoft.finamer.invoicing.application.query.manageRoomRate.getById;

import com.kynsof.share.core.domain.bus.query.IQuery;
import lombok.Getter;

import java.util.UUID;

@Getter
public class FindRoomRateByIdQuery  implements IQuery {

    private final UUID id;

    public FindRoomRateByIdQuery(UUID id) {
        this.id = id;
    }

}

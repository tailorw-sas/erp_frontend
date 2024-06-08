package com.kynsoft.notification.application.query.advertisingcontent.getById;

import com.kynsof.share.core.domain.bus.query.IQuery;
import lombok.Getter;

import java.util.UUID;

@Getter
public class FindAdvertisingContentByIdQuery  implements IQuery {

    private UUID id;

    public FindAdvertisingContentByIdQuery(UUID id) {
        this.id = id;
    }

}

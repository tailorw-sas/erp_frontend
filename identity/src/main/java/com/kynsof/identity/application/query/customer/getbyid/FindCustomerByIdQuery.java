package com.kynsof.identity.application.query.customer.getbyid;

import com.kynsof.share.core.domain.bus.query.IQuery;
import lombok.Getter;

import java.util.UUID;

@Getter
public class FindCustomerByIdQuery  implements IQuery {

    private final UUID id;

    public FindCustomerByIdQuery(UUID id) {
        this.id = id;
    }

}

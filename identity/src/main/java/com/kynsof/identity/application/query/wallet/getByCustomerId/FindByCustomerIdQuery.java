package com.kynsof.identity.application.query.wallet.getByCustomerId;

import com.kynsof.share.core.domain.bus.query.IQuery;
import lombok.Getter;

import java.util.UUID;

@Getter
public class FindByCustomerIdQuery implements IQuery {

    private final UUID customerId;

    public FindByCustomerIdQuery(UUID customerId) {
        this.customerId = customerId;
    }

}

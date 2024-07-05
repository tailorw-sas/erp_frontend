package com.kynsoft.finamer.payment.application.query.payment.getById;

import com.kynsof.share.core.domain.bus.query.IQuery;
import lombok.Getter;

import java.util.UUID;

@Getter
public class FindPaymentByIdQuery  implements IQuery {

    private final UUID id;

    public FindPaymentByIdQuery(UUID id) {
        this.id = id;
    }

}

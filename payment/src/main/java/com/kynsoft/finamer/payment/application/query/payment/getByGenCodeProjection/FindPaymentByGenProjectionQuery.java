package com.kynsoft.finamer.payment.application.query.payment.getByGenCodeProjection;

import com.kynsof.share.core.domain.bus.query.IQuery;
import lombok.Getter;

@Getter
public class FindPaymentByGenProjectionQuery  implements IQuery {

    private final long id;

    public FindPaymentByGenProjectionQuery(long id) {
        this.id = id;
    }

}

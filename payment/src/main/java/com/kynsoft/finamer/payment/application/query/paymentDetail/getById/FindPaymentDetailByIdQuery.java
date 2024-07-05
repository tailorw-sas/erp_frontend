package com.kynsoft.finamer.payment.application.query.paymentDetail.getById;

import com.kynsof.share.core.domain.bus.query.IQuery;
import lombok.Getter;

import java.util.UUID;

@Getter
public class FindPaymentDetailByIdQuery  implements IQuery {

    private final UUID id;

    public FindPaymentDetailByIdQuery(UUID id) {
        this.id = id;
    }

}

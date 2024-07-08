package com.kynsoft.finamer.payment.application.query.paymentcloseoperation.getById;

import com.kynsof.share.core.domain.bus.query.IQuery;
import lombok.Getter;

import java.util.UUID;

@Getter
public class FindPaymentCloseOperationByIdQuery  implements IQuery {

    private final UUID id;

    public FindPaymentCloseOperationByIdQuery(UUID id) {
        this.id = id;
    }

}

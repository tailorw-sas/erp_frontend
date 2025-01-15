package com.kynsoft.finamer.payment.application.query.paymentDetail.getByIdInWrite;

import com.kynsof.share.core.domain.bus.query.IQuery;
import lombok.Getter;

import java.util.UUID;

@Getter
public class FindPaymentDetailByIdInWriteQuery  implements IQuery {

    private final UUID id;

    public FindPaymentDetailByIdInWriteQuery(UUID id) {
        this.id = id;
    }

}

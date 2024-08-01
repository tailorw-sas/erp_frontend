package com.kynsoft.finamer.payment.application.query.paymentStatusHistory.getById;

import com.kynsof.share.core.domain.bus.query.IQuery;
import lombok.Getter;

import java.util.UUID;

@Getter
public class FindPaymentAttachmentStatusHistoryByIdQuery  implements IQuery {

    private final UUID id;

    public FindPaymentAttachmentStatusHistoryByIdQuery(UUID id) {
        this.id = id;
    }

}

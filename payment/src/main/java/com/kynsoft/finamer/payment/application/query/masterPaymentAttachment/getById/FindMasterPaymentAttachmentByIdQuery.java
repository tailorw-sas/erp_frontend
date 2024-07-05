package com.kynsoft.finamer.payment.application.query.masterPaymentAttachment.getById;

import com.kynsof.share.core.domain.bus.query.IQuery;
import lombok.Getter;

import java.util.UUID;

@Getter
public class FindMasterPaymentAttachmentByIdQuery  implements IQuery {

    private final UUID id;

    public FindMasterPaymentAttachmentByIdQuery(UUID id) {
        this.id = id;
    }

}

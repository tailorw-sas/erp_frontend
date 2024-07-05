package com.kynsoft.finamer.payment.application.query.attachmentType.getById;

import com.kynsof.share.core.domain.bus.query.IQuery;
import lombok.Getter;

import java.util.UUID;

@Getter
public class FindAttachmentTypeByIdQuery  implements IQuery {

    private final UUID id;

    public FindAttachmentTypeByIdQuery(UUID id) {
        this.id = id;
    }

}

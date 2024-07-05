package com.kynsoft.finamer.invoicing.application.query.manageAttachment.getById;

import com.kynsof.share.core.domain.bus.query.IQuery;
import lombok.Getter;

import java.util.UUID;

@Getter
public class FindAttachmentByIdQuery  implements IQuery {

    private final UUID id;

    public FindAttachmentByIdQuery(UUID id) {
        this.id = id;
    }

}

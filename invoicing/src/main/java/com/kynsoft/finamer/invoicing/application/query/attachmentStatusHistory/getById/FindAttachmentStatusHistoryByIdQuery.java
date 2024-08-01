package com.kynsoft.finamer.invoicing.application.query.attachmentStatusHistory.getById;

import com.kynsof.share.core.domain.bus.query.IQuery;
import lombok.Getter;

import java.util.UUID;

@Getter
public class FindAttachmentStatusHistoryByIdQuery  implements IQuery {

    private final UUID id;

    public FindAttachmentStatusHistoryByIdQuery(UUID id) {
        this.id = id;
    }

}

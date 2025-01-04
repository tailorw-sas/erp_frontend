package com.kynsoft.finamer.payment.application.query.shareFile.getById;

import com.kynsof.share.core.domain.bus.query.IQuery;
import lombok.Getter;

import java.util.UUID;

@Getter
public class FindPaymentShareFileByIdQuery implements IQuery {

    private final UUID id;

    public FindPaymentShareFileByIdQuery(UUID id) {
        this.id = id;
    }

}

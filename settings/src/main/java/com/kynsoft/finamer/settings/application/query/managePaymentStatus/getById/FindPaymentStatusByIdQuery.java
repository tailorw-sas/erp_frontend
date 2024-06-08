package com.kynsoft.finamer.settings.application.query.managePaymentStatus.getById;

import com.kynsof.share.core.domain.bus.query.IQuery;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class FindPaymentStatusByIdQuery implements IQuery {
    private final UUID id;
}

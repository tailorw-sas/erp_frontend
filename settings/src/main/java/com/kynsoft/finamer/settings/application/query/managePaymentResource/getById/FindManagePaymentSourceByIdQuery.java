package com.kynsoft.finamer.settings.application.query.managePaymentResource.getById;

import com.kynsof.share.core.domain.bus.query.IQuery;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@AllArgsConstructor
@Getter
public class FindManagePaymentSourceByIdQuery implements IQuery {

    private UUID id;
}

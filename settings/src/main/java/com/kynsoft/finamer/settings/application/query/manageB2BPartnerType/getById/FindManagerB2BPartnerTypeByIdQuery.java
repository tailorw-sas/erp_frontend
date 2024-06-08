package com.kynsoft.finamer.settings.application.query.manageB2BPartnerType.getById;

import com.kynsof.share.core.domain.bus.query.IQuery;
import lombok.Getter;

import java.util.UUID;

@Getter
public class FindManagerB2BPartnerTypeByIdQuery  implements IQuery {

    private final UUID id;

    public FindManagerB2BPartnerTypeByIdQuery(UUID id) {
        this.id = id;
    }

}

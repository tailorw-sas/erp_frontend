package com.kynsoft.finamer.settings.application.query.manageB2BPartner.getById;

import com.kynsof.share.core.domain.bus.query.IQuery;
import lombok.Getter;

import java.util.UUID;

@Getter
public class FindManagerB2BPartnerByIdQuery  implements IQuery {

    private final UUID id;

    public FindManagerB2BPartnerByIdQuery(UUID id) {
        this.id = id;
    }

}

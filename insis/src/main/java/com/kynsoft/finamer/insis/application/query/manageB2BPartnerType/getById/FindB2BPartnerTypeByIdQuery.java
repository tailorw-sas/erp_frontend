package com.kynsoft.finamer.insis.application.query.manageB2BPartnerType.getById;

import com.kynsof.share.core.domain.bus.query.IQuery;
import lombok.Getter;

import java.util.UUID;

@Getter
public class FindB2BPartnerTypeByIdQuery implements IQuery {
    private final UUID id;

    public FindB2BPartnerTypeByIdQuery(UUID id){
        this.id = id;
    }
}

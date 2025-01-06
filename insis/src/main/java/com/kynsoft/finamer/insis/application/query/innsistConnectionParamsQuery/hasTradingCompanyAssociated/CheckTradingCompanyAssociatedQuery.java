package com.kynsoft.finamer.insis.application.query.innsistConnectionParamsQuery.hasTradingCompanyAssociated;

import com.kynsof.share.core.domain.bus.query.IQuery;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CheckTradingCompanyAssociatedQuery implements IQuery {

    private final UUID id;

    public CheckTradingCompanyAssociatedQuery(UUID id){
        this.id = id;
    }
}

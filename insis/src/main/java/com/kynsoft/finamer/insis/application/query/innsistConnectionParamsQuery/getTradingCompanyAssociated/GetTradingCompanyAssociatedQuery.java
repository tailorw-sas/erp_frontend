package com.kynsoft.finamer.insis.application.query.innsistConnectionParamsQuery.getTradingCompanyAssociated;

import com.kynsof.share.core.domain.bus.query.IQuery;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class GetTradingCompanyAssociatedQuery implements IQuery {

    public final UUID id;

    public GetTradingCompanyAssociatedQuery(UUID id){
        this.id = id;
    }
}

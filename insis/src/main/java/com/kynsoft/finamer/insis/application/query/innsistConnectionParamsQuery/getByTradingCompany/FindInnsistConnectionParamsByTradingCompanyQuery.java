package com.kynsoft.finamer.insis.application.query.innsistConnectionParamsQuery.getByTradingCompany;

import com.kynsof.share.core.domain.bus.query.IQuery;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class FindInnsistConnectionParamsByTradingCompanyQuery implements IQuery {
    private final UUID tradingCompanyId;

    public FindInnsistConnectionParamsByTradingCompanyQuery(UUID tradingCompanyId){
        this.tradingCompanyId = tradingCompanyId;
    }
}

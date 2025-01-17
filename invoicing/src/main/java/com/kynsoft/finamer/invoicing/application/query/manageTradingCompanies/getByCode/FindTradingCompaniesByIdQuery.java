package com.kynsoft.finamer.invoicing.application.query.manageTradingCompanies.getByCode;

import com.kynsof.share.core.domain.bus.query.IQuery;
import lombok.Getter;

@Getter
public class FindTradingCompaniesByIdQuery  implements IQuery {

    private final String code;

    public FindTradingCompaniesByIdQuery(String code) {
        this.code = code;
    }

}

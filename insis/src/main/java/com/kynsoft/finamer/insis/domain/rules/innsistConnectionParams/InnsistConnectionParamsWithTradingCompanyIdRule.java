package com.kynsoft.finamer.insis.domain.rules.innsistConnectionParams;

import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.rules.BusinessRule;
import com.kynsoft.finamer.insis.domain.dto.InnsistConnectionParamsDto;

import java.util.Objects;

public class InnsistConnectionParamsWithTradingCompanyIdRule extends BusinessRule {

    private final InnsistConnectionParamsDto connectionParamsDto;

    public InnsistConnectionParamsWithTradingCompanyIdRule(InnsistConnectionParamsDto connectionParamsDto){
        super(DomainErrorMessage.INNSIST_CONNECTION_PARAMETER_WITHOUT_TRADING_COMPANY, new ErrorField("trading_company_id", "The connection parameter must be associated with a trading company."));
        this.connectionParamsDto = connectionParamsDto;
    }

    @Override
    public boolean isBroken() {
        return Objects.isNull(connectionParamsDto) ? true : false ;
    }
}

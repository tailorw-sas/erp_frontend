package com.kynsoft.finamer.insis.application.query.objectResponse.innsistConnectionParams;

import com.kynsof.share.core.domain.bus.query.IResponse;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HasTradingCompanyAssociatedResponse implements IResponse {
    public boolean hasTradingCompanyAssociated;

    public HasTradingCompanyAssociatedResponse(boolean hasTradingCompanyAssociated){
        this.hasTradingCompanyAssociated = hasTradingCompanyAssociated;
    }
}

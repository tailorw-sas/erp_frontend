package com.kynsoft.finamer.insis.domain.rules.innsistConnectionParams;

import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.rules.BusinessRule;

public class InnsistConnectionParamsPortNumberRule extends BusinessRule {

    private final int portNumber;

    public InnsistConnectionParamsPortNumberRule(int portNumber){
        super(DomainErrorMessage.INNSIST_CONNECTION_PARAMETER_PORT_NUMBER_INVALID, new ErrorField("portNumber", "The port number must be between 0 and 65535"));
        this.portNumber = portNumber;
    }

    @Override
    public boolean isBroken() {
        if(portNumber < 0 || portNumber > 65535){
            return true;
        }
        return false;
    }
}

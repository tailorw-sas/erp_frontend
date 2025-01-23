package com.kynsoft.finamer.insis.domain.rules.innsistConnectionParams;

import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.rules.BusinessRule;

public class InnsistConnectionParamsHostNameRule extends BusinessRule {

    private final String hostname;
    private static String HOSTNAME_PATTERN = "^([a-zA-Z0-9]([a-zA-Z0-9\\-]{0,61}[a-zA-Z0-9])?\\.)+[a-zA-Z]{2,}$";
    private static String IPV4_PATTERN = "^([0-9]{1,3}\\.){3}[0-9]{1,3}$";
    private static String IPV6_PATTERN = "^([0-9a-fA-F]{1,4}:){7}[0-9a-fA-F]{1,4}$";

    public InnsistConnectionParamsHostNameRule(String hostname){
        super(
                DomainErrorMessage.INNSIST_CONNECTION_PARAMETER_HOSTNAME_INVALID,
                new ErrorField("hostname", "The hostname value is not accepted.")
        );
        this.hostname = hostname;
    }

    @Override
    public boolean isBroken() {
        if(!isIpv4String(hostname) && !isIpv6String(hostname) && !isHostnameString(hostname)){
            return true;
        }
        return false;
    }

    private static boolean isIpv4String(String input){
        return input.matches(IPV4_PATTERN);
    }

    private static boolean isIpv6String(String input){
        return input.matches(IPV6_PATTERN);
    }

    private static boolean isHostnameString(String input){
        return input.matches(HOSTNAME_PATTERN);
    }
}

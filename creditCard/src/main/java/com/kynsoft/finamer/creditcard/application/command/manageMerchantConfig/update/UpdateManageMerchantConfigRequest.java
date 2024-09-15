package com.kynsoft.finamer.creditcard.application.command.manageMerchantConfig.update;

import com.kynsoft.finamer.creditcard.domain.dtoEnum.Method;
import lombok.Data;

import java.util.UUID;

@Data
public class UpdateManageMerchantConfigRequest {

    private UUID manageMerchant;
    private String url;
    private String altUrl;
    private String successUrl;
    private String errorUrl;
    private String declinedUrl;
    private String merchantType;
    private String name;
    private Method method;
    private String institutionCode;
    private String merchantNumber;
    private String merchantTerminal;
}

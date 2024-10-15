package com.kynsoft.finamer.creditcard.application.command.merchantLanguageCode.update;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class UpdateMerchantLanguageCodeRequest {

    private String name;
    private String merchantLanguage;
    private UUID manageLanguage;
    private UUID manageMerchant;
}

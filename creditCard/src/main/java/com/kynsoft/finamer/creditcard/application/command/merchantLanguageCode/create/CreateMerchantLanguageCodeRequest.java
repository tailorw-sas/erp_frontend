package com.kynsoft.finamer.creditcard.application.command.merchantLanguageCode.create;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateMerchantLanguageCodeRequest {

    private String name;
    private String merchantLanguage;
    private UUID manageLanguage;
    private UUID manageMerchant;
}

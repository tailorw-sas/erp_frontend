package com.kynsoft.finamer.creditcard.application.command.merchantLanguageCode.create;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class CreateMerchantLanguageCodeMessage implements ICommandMessage {

    private final UUID id;
    private final String command = "CREATE_MERCHANT_LANGUAGE_CODE";
}

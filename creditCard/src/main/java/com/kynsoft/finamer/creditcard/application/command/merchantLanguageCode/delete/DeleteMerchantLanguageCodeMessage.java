package com.kynsoft.finamer.creditcard.application.command.merchantLanguageCode.delete;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class DeleteMerchantLanguageCodeMessage implements ICommandMessage {

    private final UUID id;
    private final String command = "DELETE_MERCHANT_LANGUAGE_CODE";
}

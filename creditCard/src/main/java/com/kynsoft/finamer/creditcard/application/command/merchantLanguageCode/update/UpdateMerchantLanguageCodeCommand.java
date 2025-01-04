package com.kynsoft.finamer.creditcard.application.command.merchantLanguageCode.update;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class UpdateMerchantLanguageCodeCommand implements ICommand {

    private UUID id;
    private String name;
    private String merchantLanguage;
    private UUID manageLanguage;
    private UUID manageMerchant;

    public static UpdateMerchantLanguageCodeCommand fromRequest(UpdateMerchantLanguageCodeRequest request, UUID id){
        return new UpdateMerchantLanguageCodeCommand(
                id, request.getName(), request.getMerchantLanguage(),
                request.getManageLanguage(), request.getManageMerchant()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new UpdateMerchantLanguageCodeMessage(id);
    }
}

package com.kynsoft.finamer.creditcard.application.command.merchantLanguageCode.create;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateMerchantLanguageCodeCommand implements ICommand {

    private UUID id;
    private String name;
    private String merchantLanguage;
    private UUID manageLanguage;
    private UUID manageMerchant;

    public CreateMerchantLanguageCodeCommand(String name, String merchantLanguage, UUID manageLanguage, UUID manageMerchant) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.merchantLanguage = merchantLanguage;
        this.manageLanguage = manageLanguage;
        this.manageMerchant = manageMerchant;
    }

    public static CreateMerchantLanguageCodeCommand fromRequest(CreateMerchantLanguageCodeRequest request){
        return new CreateMerchantLanguageCodeCommand(
                request.getName(), request.getMerchantLanguage(),
                request.getManageLanguage(), request.getManageMerchant()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new CreateMerchantLanguageCodeMessage(id);
    }
}

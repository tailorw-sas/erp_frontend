package com.kynsoft.finamer.creditcard.application.command.manageMerchantConfig.create;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsoft.finamer.creditcard.domain.dtoEnum.Method;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class CreateManageMerchantConfigCommand implements ICommand {
    private UUID id;
    private UUID manageMerchant;
    private String url;
    private String altUrl;
    private String successUrl;
    private String errorUrl;
    private String declinedUrl;
    private String merchantType;
    private String name;
    private String method;
    private String institutionCode;
    private String merchantNumber;
    private String merchantTerminal;

    @Override
    public ICommandMessage getMessage() {
        return new CreateManageMerchantConfigMessage(id);
    }
}

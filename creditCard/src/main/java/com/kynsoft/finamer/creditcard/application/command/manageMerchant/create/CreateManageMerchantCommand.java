package com.kynsoft.finamer.creditcard.application.command.manageMerchant.create;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsoft.finamer.creditcard.domain.dtoEnum.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class CreateManageMerchantCommand implements ICommand {

    private UUID id;
    private String code;
    private String description;
    private UUID b2bPartner;
    private Boolean defaultm;
    private String status;

    @Override
    public ICommandMessage getMessage() {
        return new CreateManageMerchantMessage(id);
    }
}

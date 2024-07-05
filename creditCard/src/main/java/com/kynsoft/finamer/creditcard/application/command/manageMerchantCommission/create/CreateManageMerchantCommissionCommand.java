package com.kynsoft.finamer.creditcard.application.command.manageMerchantCommission.create;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class CreateManageMerchantCommissionCommand implements ICommand {

    private final UUID id;
    private final UUID managerMerchant;
    private final UUID manageCreditCartType;
    private final Double commission;
    private final String calculationType;

    @Override
    public ICommandMessage getMessage() {
        return new CreateManageMerchantCommissionMessage(id);
    }
}

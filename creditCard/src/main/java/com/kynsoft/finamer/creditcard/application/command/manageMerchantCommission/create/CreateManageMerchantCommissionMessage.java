package com.kynsoft.finamer.creditcard.application.command.manageMerchantCommission.create;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class CreateManageMerchantCommissionMessage implements ICommandMessage {

    private final UUID id;

    private final String command = "CREATE_MANAGE_MERCHANT_CCOMMISSION";

    public CreateManageMerchantCommissionMessage(UUID id) {
        this.id = id;
    }

}

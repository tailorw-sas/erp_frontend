package com.kynsoft.finamer.creditcard.application.command.manageMerchantCommission.update;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class UpdateManageMerchantCommissionMessage implements ICommandMessage {

    private final UUID id;

    private final String command = "UPDATE_MANAGE_MERCHANT_CCOMMISSION";

    public UpdateManageMerchantCommissionMessage(UUID id) {
        this.id = id;
    }

}

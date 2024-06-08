package com.kynsoft.finamer.settings.application.command.manageMerchantCommission.delete;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class DeleteManageMerchantCommissionMessage implements ICommandMessage {

    private final UUID id;

    private final String command = "DELETE_MANAGE_MERCHANT_CCOMMISSION";

    public DeleteManageMerchantCommissionMessage(UUID id) {
        this.id = id;
    }

}

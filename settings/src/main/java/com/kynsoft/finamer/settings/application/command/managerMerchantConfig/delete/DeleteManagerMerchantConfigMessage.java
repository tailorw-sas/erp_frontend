package com.kynsoft.finamer.settings.application.command.managerMerchantConfig.delete;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Data;

import java.util.UUID;

@Data
public class DeleteManagerMerchantConfigMessage implements ICommandMessage{

    private final UUID id;

    private final String command = "DELETE_MANAGER_MERCHANT_CONFIG";

    public DeleteManagerMerchantConfigMessage(UUID id) {
        this.id = id;
    }

}

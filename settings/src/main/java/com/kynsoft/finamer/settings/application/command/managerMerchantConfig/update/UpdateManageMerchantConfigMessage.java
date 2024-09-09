package com.kynsoft.finamer.settings.application.command.managerMerchantConfig.update;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Data;

import java.util.UUID;

@Data
public class UpdateManageMerchantConfigMessage implements ICommandMessage {
    private UUID uuid;
    private String command = "UPDATE_MANAGER_MERCHANT_CONFIG";



    public UpdateManageMerchantConfigMessage(UUID uuid) {
        this.uuid = uuid;
    }
}

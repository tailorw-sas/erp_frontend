package com.kynsoft.finamer.settings.application.command.managerMerchantConfig.create;


import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Data;

import java.util.UUID;


@Data
public class CreateManageMerchantConfigMessage implements ICommandMessage {
private UUID uuid;
private String command = "CREATE_MANAGER_MERCHANT_CONFIG";



    public CreateManageMerchantConfigMessage(UUID uuid) {
        this.uuid = uuid;
    }
}

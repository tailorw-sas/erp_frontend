package com.kynsoft.finamer.settings.application.command.managerMerchantHotelEnrolle.update;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class UpdateManagerMerchantHotelEnrolleMessage implements ICommandMessage {

    private final UUID id;

    private final String command = "UPDATE_MANAGER_MERCHANT_HOTEL_ENROLLE";

    public UpdateManagerMerchantHotelEnrolleMessage(UUID id) {
        this.id = id;
    }

}

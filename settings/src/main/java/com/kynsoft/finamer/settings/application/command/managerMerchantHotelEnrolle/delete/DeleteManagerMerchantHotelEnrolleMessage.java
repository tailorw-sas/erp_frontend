package com.kynsoft.finamer.settings.application.command.managerMerchantHotelEnrolle.delete;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class DeleteManagerMerchantHotelEnrolleMessage implements ICommandMessage {

    private final UUID id;

    private final String command = "DELETE_MANAGER_MERCHANT_HOTEL_ENROLLE";

    public DeleteManagerMerchantHotelEnrolleMessage(UUID id) {
        this.id = id;
    }

}

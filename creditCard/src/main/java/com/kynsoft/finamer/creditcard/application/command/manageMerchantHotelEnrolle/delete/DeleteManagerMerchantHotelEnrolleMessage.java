package com.kynsoft.finamer.creditcard.application.command.manageMerchantHotelEnrolle.delete;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class DeleteManagerMerchantHotelEnrolleMessage implements ICommandMessage {

    private final UUID id;

    private final String command = "DELETE_MANAGE_MERCHANT_HOTEL_ENROLLE";

    public DeleteManagerMerchantHotelEnrolleMessage(UUID id) {
        this.id = id;
    }

}

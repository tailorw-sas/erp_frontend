package com.kynsoft.finamer.creditcard.application.command.manageMerchantHotelEnrolle.create;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class CreateManagerMerchantHotelEnrolleMessage implements ICommandMessage {

    private final UUID id;

    private final String command = "CREATE_MANAGER_MERCHANT_CURRENCY";

    public CreateManagerMerchantHotelEnrolleMessage(UUID id) {
        this.id = id;
    }

}

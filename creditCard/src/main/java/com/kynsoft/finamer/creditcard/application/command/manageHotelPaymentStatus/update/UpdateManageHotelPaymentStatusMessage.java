package com.kynsoft.finamer.creditcard.application.command.manageHotelPaymentStatus.update;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class UpdateManageHotelPaymentStatusMessage implements ICommandMessage {

    private final UUID id;

    private final String command = "UPDATE_MANAGE_HOTEL_PAYMENT_STATUS";

    public UpdateManageHotelPaymentStatusMessage(UUID id) {
        this.id = id;
    }
}

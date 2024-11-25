package com.kynsoft.finamer.creditcard.application.command.manageHotelPaymentStatus.create;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class CreateManageHotelPaymentStatusMessage implements ICommandMessage {

    private final UUID id;

    private final String command = "CREATE_MANAGE_HOTEL_PAYMENT_STATUS";

    public CreateManageHotelPaymentStatusMessage(UUID id) {
        this.id = id;
    }
}

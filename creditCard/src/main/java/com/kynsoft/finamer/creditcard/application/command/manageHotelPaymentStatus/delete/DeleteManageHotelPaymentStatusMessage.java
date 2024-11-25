package com.kynsoft.finamer.creditcard.application.command.manageHotelPaymentStatus.delete;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class DeleteManageHotelPaymentStatusMessage implements ICommandMessage {

    private final UUID id;

    private final String command = "DELETE_HOTEL_PAYMENT_STATUS";

    public DeleteManageHotelPaymentStatusMessage(UUID id) {
        this.id = id;
    }
}

package com.kynsoft.finamer.invoicing.application.command.manageBooking.createMany;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Getter
public class CreateManyBookingMessage implements ICommandMessage {

    private final List<UUID> idList;

    private final String command = "CREATE_MANY_BOOKING";

    public CreateManyBookingMessage(List<UUID> idList) {
        this.idList = idList;
    }

}

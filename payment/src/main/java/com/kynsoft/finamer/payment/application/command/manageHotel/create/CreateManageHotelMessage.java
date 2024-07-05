package com.kynsoft.finamer.payment.application.command.manageHotel.create;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateManageHotelMessage implements ICommandMessage {

    private final UUID id;

    private final String command = "CREATE_MANAGE_HOTEL";

    public CreateManageHotelMessage(UUID id) {
        this.id = id;
    }
}

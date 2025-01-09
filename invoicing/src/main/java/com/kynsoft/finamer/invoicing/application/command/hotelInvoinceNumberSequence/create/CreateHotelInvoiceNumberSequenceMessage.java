package com.kynsoft.finamer.invoicing.application.command.hotelInvoinceNumberSequence.create;

import com.kynsoft.finamer.invoicing.application.command.resourceType.create.*;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateHotelInvoiceNumberSequenceMessage implements ICommandMessage {

    private final UUID id;

    private final String command = "CREATE_MANAGE_RESOURCE_TYPE";

    public CreateHotelInvoiceNumberSequenceMessage(UUID id) {
        this.id = id;
    }
}

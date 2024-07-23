package com.kynsoft.finamer.payment.application.command.manageInvoiceStatus.create;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class CreateManageInvoiceStatusMessage implements ICommandMessage {

    private final UUID id;

    public CreateManageInvoiceStatusMessage(UUID id) {
        this.id = id;
    }
}

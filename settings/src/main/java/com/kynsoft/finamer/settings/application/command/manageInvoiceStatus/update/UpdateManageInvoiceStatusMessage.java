package com.kynsoft.finamer.settings.application.command.manageInvoiceStatus.update;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class UpdateManageInvoiceStatusMessage implements ICommandMessage {

    private final UUID id;

    private final String command = "UPDATE_MANAGE_INVOICE_STATUS";

    public UpdateManageInvoiceStatusMessage(UUID id) {
        this.id = id;
    }
}

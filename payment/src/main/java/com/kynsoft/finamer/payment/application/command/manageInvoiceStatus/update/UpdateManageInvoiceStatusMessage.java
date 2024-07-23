package com.kynsoft.finamer.payment.application.command.manageInvoiceStatus.update;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class UpdateManageInvoiceStatusMessage implements ICommandMessage {

    private final UUID id;

    public UpdateManageInvoiceStatusMessage(UUID id) {
        this.id = id;
    }
}

package com.kynsoft.finamer.payment.application.command.manageInvoiceType.update;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class UpdateManageInvoiceTypeMessage implements ICommandMessage {

    private final UUID id;

    public UpdateManageInvoiceTypeMessage(UUID id) {
        this.id = id;
    }
}

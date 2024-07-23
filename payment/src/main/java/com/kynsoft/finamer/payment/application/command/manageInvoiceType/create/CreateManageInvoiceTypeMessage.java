package com.kynsoft.finamer.payment.application.command.manageInvoiceType.create;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class CreateManageInvoiceTypeMessage implements ICommandMessage {

    private final UUID id;

    public CreateManageInvoiceTypeMessage(UUID id) {
        this.id = id;
    }
}

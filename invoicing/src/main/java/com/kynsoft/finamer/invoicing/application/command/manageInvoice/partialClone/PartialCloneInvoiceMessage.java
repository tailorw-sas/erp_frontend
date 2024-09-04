package com.kynsoft.finamer.invoicing.application.command.manageInvoice.partialClone;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class PartialCloneInvoiceMessage implements ICommandMessage {

    private final String command = "PARTIAL_CLONE_INVOICE";
    private UUID id;
    private UUID cloned;

   

    public PartialCloneInvoiceMessage(UUID id, UUID cloned) {
        this.id = id;
        this.cloned = cloned;
    }

}

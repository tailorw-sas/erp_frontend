package com.kynsoft.finamer.invoicing.application.command.manageInvoice.totalClone;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class TotalCloneMessage implements ICommandMessage {

    private final String command = "TOTAL_CLONE_INVOICE";
    private UUID invoiceToClone;
    private UUID clonedInvoice;
    private Long clonedInvoiceId;
    private String clonedInvoiceNo;
}

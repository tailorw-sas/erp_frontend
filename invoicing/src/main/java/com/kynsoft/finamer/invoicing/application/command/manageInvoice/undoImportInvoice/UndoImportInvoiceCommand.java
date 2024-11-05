package com.kynsoft.finamer.invoicing.application.command.manageInvoice.undoImportInvoice;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class UndoImportInvoiceCommand implements ICommand {

    private List<UUID> ids;
    private IMediator mediator;

    public static UndoImportInvoiceCommand fromRequest(UndoImportInvoiceRequest request, IMediator mediator) {
        return new UndoImportInvoiceCommand(request.getIds(), mediator);
    }

    @Override
    public ICommandMessage getMessage() {
        return new UndoImportInvoiceMessage();
    }

}

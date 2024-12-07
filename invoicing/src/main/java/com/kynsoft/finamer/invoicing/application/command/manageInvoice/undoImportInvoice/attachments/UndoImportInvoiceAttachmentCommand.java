package com.kynsoft.finamer.invoicing.application.command.manageInvoice.undoImportInvoice.attachments;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsoft.finamer.invoicing.domain.dto.ManageAttachmentDto;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UndoImportInvoiceAttachmentCommand implements ICommand {

    private List<ManageAttachmentDto> objects;

    @Override
    public ICommandMessage getMessage() {
        return new UndoImportInvoiceAttachmentMessage();
    }

}

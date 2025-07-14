package com.kynsoft.finamer.insis.application.command.roomRate.undoImportRoomRate;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class UndoImportRoomRateCommand implements ICommand {

    private UUID invoiceId;

    public UndoImportRoomRateCommand(UUID invoiceId){
        this.invoiceId = invoiceId;
    }

    @Override
    public ICommandMessage getMessage() {
        return new UndoImportRoomRateMessage(invoiceId);
    }
}

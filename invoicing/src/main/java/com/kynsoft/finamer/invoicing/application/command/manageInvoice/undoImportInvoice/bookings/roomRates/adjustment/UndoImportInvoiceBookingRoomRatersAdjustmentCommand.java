package com.kynsoft.finamer.invoicing.application.command.manageInvoice.undoImportInvoice.bookings.roomRates.adjustment;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsoft.finamer.invoicing.domain.dto.ManageAdjustmentDto;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UndoImportInvoiceBookingRoomRatersAdjustmentCommand implements ICommand {

    private List<ManageAdjustmentDto> objects;

    @Override
    public ICommandMessage getMessage() {
        return new UndoImportInvoiceBookingRoomRatersAdjustmentMessage();
    }

}

package com.kynsoft.finamer.invoicing.application.command.manageInvoice.undoImportInvoice.bookings.roomRates;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.invoicing.application.command.manageInvoice.undoImportInvoice.bookings.roomRates.adjustment.UndoImportInvoiceBookingRoomRatersAdjustmentCommand;
import com.kynsoft.finamer.invoicing.domain.dto.ManageAdjustmentDto;
import com.kynsoft.finamer.invoicing.domain.services.IManageAdjustmentService;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UndoImportInvoiceBookingRoomRatersCommandHandler implements ICommandHandler<UndoImportInvoiceBookingRoomRatersCommand> {

    private final IManageAdjustmentService adjustmentService;

    public UndoImportInvoiceBookingRoomRatersCommandHandler(IManageAdjustmentService adjustmentService) {
        this.adjustmentService = adjustmentService;
    }

    @Override
    public void handle(UndoImportInvoiceBookingRoomRatersCommand command) {
        command.getObjects().forEach(rate -> {
            List<ManageAdjustmentDto> adjustmentDtoList = this.adjustmentService.findByRoomRateId(rate.getId());
            if (adjustmentDtoList != null && !adjustmentDtoList.isEmpty()) {
                command.getMediator().send(new UndoImportInvoiceBookingRoomRatersAdjustmentCommand(adjustmentDtoList));
            }
            rate.setDeleteInvoice(true);
        });
    }

}

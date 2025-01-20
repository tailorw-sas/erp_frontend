package com.kynsoft.finamer.invoicing.application.command.manageInvoice.undoImportInvoice.bookings;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.invoicing.application.command.manageInvoice.undoImportInvoice.bookings.roomRates.UndoImportInvoiceBookingRoomRatersCommand;
import com.kynsoft.finamer.invoicing.domain.dto.ManageRoomRateDto;
import com.kynsoft.finamer.invoicing.domain.services.IManageRoomRateService;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UndoImportInvoiceBookingCommandHandler implements ICommandHandler<UndoImportInvoiceBookingCommand> {

    private final IManageRoomRateService roomRateService;

    public UndoImportInvoiceBookingCommandHandler(IManageRoomRateService roomRateService) {
        this.roomRateService = roomRateService;
    }

    @Override
    public void handle(UndoImportInvoiceBookingCommand command) {
        command.getObjects().forEach(booking -> {
            List<ManageRoomRateDto> roomRateDtoList = this.roomRateService.findByBooking(booking.getId());
            if (roomRateDtoList != null && !roomRateDtoList.isEmpty()) {
                command.getMediator().send(new UndoImportInvoiceBookingRoomRatersCommand(roomRateDtoList, command.getMediator()));
            }
            booking.setDeleteInvoice(true);
        });
    }

}

package com.kynsoft.finamer.invoicing.application.command.manageInvoice.undoImportInvoice;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.invoicing.application.command.manageInvoice.undoImportInvoice.attachments.UndoImportInvoiceAttachmentCommand;
import com.kynsoft.finamer.invoicing.application.command.manageInvoice.undoImportInvoice.bookings.UndoImportInvoiceBookingCommand;
import com.kynsoft.finamer.invoicing.domain.dto.ManageInvoiceDto;
import com.kynsoft.finamer.invoicing.domain.dto.ManageInvoiceStatusDto;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.EInvoiceStatus;
import com.kynsoft.finamer.invoicing.domain.services.IManageInvoiceService;
import com.kynsoft.finamer.invoicing.domain.services.IManageInvoiceStatusService;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class UndoImportInvoiceCommandHandler implements ICommandHandler<UndoImportInvoiceCommand> {

    private final IManageInvoiceService service;
    private final IManageInvoiceStatusService invoiceStatusService;

    public UndoImportInvoiceCommandHandler(IManageInvoiceService service,
            IManageInvoiceStatusService invoiceStatusService) {
        this.service = service;
        this.invoiceStatusService = invoiceStatusService;
    }

    @Override
    public void handle(UndoImportInvoiceCommand command) {
        List<UndoImportErrors> errors = new ArrayList<>();
        for (UUID id : command.getIds()) {
            ManageInvoiceDto delete = this.service.findById(id);
            if (delete.getManageInvoiceStatus().getProcessStatus()) {
                if (delete.getBookings() != null || !delete.getBookings().isEmpty()) {
                    command.getMediator().send(new UndoImportInvoiceBookingCommand(delete.getBookings(), command.getMediator()));
                }
                if (delete.getAttachments() != null || !delete.getAttachments().isEmpty()) {
                    command.getMediator().send(new UndoImportInvoiceAttachmentCommand(delete.getAttachments()));
                }
                ManageInvoiceStatusDto invoiceStatusDto = this.invoiceStatusService.findByCanceledStatus();
                delete.setManageInvoiceStatus(invoiceStatusDto);
                delete.setStatus(EInvoiceStatus.CANCELED);
                this.service.deleteInvoice(delete);
            } else {
                errors.add(new UndoImportErrors(id, delete.getInvoiceNo(), delete.getManageInvoiceStatus().getCode() + "-" + delete.getManageInvoiceStatus().getName()));
            }
        }
        command.setErrors(errors);
    }

}

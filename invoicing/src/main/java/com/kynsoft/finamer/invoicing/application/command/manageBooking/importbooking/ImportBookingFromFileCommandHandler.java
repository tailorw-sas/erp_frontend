package com.kynsoft.finamer.invoicing.application.command.manageBooking.importbooking;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.invoicing.domain.services.ImportBookingService;
import org.springframework.stereotype.Component;

@Component
public class ImportBookingFromFileCommandHandler implements ICommandHandler<ImportBookingFromFileCommand> {

    private final ImportBookingService importBookingService;

    public ImportBookingFromFileCommandHandler(ImportBookingService importBookingService) {
        this.importBookingService = importBookingService;
    }

    @Override
    public void handle(ImportBookingFromFileCommand command) {
        ImportBookingFromFileRequest importBookingFromFileRequest = new ImportBookingFromFileRequest(command.getRequest());
        importBookingService.importBookingFromFile(importBookingFromFileRequest);
    }
}

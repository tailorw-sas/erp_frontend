package com.kynsoft.finamer.invoicing.application.query.manageBooking.importbooking;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsoft.finamer.invoicing.domain.excel.validators.ImportBookingProcessStatusRequest;
import com.kynsoft.finamer.invoicing.domain.services.ImportBookingService;
import org.springframework.stereotype.Component;

@Component
public class ImportBookingProcessStatusHandler implements IQueryHandler<ImportBookingProcessStatusQuery,ImportBookingProcessStatusResponse> {

    private final ImportBookingService importBookingService;

    public ImportBookingProcessStatusHandler(ImportBookingService importBookingService) {
        this.importBookingService = importBookingService;
    }

    @Override
    public ImportBookingProcessStatusResponse handle(ImportBookingProcessStatusQuery query) {;
        return importBookingService.getImportBookingProcessStatus(query.getRequest());
    }
}

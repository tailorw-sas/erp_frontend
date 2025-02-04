package com.kynsoft.finamer.insis.application.query.importProcess.getErrorResults;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.insis.domain.dto.ImportProcessDto;
import com.kynsoft.finamer.insis.domain.services.IBookingService;
import com.kynsoft.finamer.insis.domain.services.IImportBookingService;
import com.kynsoft.finamer.insis.domain.services.IImportProcessService;
import org.springframework.stereotype.Component;

@Component
public class GetErrorResultsImportProcessQueryHandler implements IQueryHandler<GetErrorResultsImportProcessQuery, PaginatedResponse> {

    private final IBookingService bookingService;
    private final IImportProcessService importProcessService;
    private final IImportBookingService importBookingService;


    public GetErrorResultsImportProcessQueryHandler(IBookingService bookingService,
                                                    IImportProcessService importProcessService,
                                                    IImportBookingService importBookingService){
        this.bookingService = bookingService;
        this.importProcessService = importProcessService;
        this.importBookingService = importBookingService;
    }

    @Override
    public PaginatedResponse handle(GetErrorResultsImportProcessQuery query) {
        ImportProcessDto importProcess = importProcessService.findById(query.getImportProcessId());
        PaginatedResponse response = importBookingService.getBookingErrorsByImportProcessId(importProcess.getId(), query.getPageable());
        return response;
    }
}

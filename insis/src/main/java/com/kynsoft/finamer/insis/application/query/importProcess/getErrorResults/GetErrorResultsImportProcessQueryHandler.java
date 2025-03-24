package com.kynsoft.finamer.insis.application.query.importProcess.getErrorResults;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.insis.domain.dto.ImportProcessDto;
import com.kynsoft.finamer.insis.domain.services.IBookingService;
import com.kynsoft.finamer.insis.domain.services.IImportRoomRateService;
import com.kynsoft.finamer.insis.domain.services.IImportProcessService;
import com.kynsoft.finamer.insis.domain.services.IRoomRateService;
import org.springframework.stereotype.Component;

@Component
public class GetErrorResultsImportProcessQueryHandler implements IQueryHandler<GetErrorResultsImportProcessQuery, PaginatedResponse> {

    private final IImportProcessService importProcessService;
    private final IImportRoomRateService importRoomRateService;


    public GetErrorResultsImportProcessQueryHandler(IImportProcessService importProcessService,
                                                    IImportRoomRateService importRoomRateService){
        this.importProcessService = importProcessService;
        this.importRoomRateService = importRoomRateService;
    }

    @Override
    public PaginatedResponse handle(GetErrorResultsImportProcessQuery query) {
        ImportProcessDto importProcess = importProcessService.findById(query.getImportProcessId());
        return importRoomRateService.getRoomRateErrorsByImportProcessId(importProcess.getId(), query.getPageable());
    }
}

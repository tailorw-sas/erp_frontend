package com.kynsoft.finamer.insis.application.query.importProcess.getById;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsoft.finamer.insis.application.query.objectResponse.importProcess.ImportProcessResponse;
import com.kynsoft.finamer.insis.domain.dto.ImportProcessDto;
import com.kynsoft.finamer.insis.domain.services.IImportProcessService;
import org.springframework.stereotype.Component;

@Component
public class GetImportProcessByIdQueryHandler implements IQueryHandler<GetImportProcessByIdQuery, ImportProcessResponse> {

    private final IImportProcessService service;

    public GetImportProcessByIdQueryHandler(IImportProcessService service){
        this.service = service;
    }

    @Override
    public ImportProcessResponse handle(GetImportProcessByIdQuery query) {
        ImportProcessDto dto = service.findById(query.getId());
        return new ImportProcessResponse(dto.getId(),
                dto.getStatus(),
                dto.getImportDate(),
                dto.getCompletedAt(),
                dto.getTotalBookings(),
                dto.getUserId()
        );
    }
}

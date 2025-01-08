package com.kynsoft.finamer.insis.domain.services;

import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.insis.domain.dto.ImportProcessDto;

import java.util.UUID;

public interface IImportProcessService {

    ImportProcessDto create(ImportProcessDto dto);

    void update(ImportProcessDto dto);

    ImportProcessDto findById(UUID id);

}

package com.kynsoft.finamer.insis.domain.services;

import com.kynsoft.finamer.insis.domain.dto.BatchProcessLogDto;
import com.kynsoft.finamer.insis.infrastructure.model.enums.BatchStatus;

import java.util.UUID;

public interface IBatchProcessLogService {

    UUID create(BatchProcessLogDto dto);

    void update(BatchProcessLogDto dto);

    void delete(BatchProcessLogDto dto);

    BatchProcessLogDto findById(UUID id);

    Long countByStatus(BatchStatus status);
}

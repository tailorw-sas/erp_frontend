package com.kynsoft.finamer.invoicing.domain.services;

import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.invoicing.domain.dto.IncomeAdjustmentDto;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface IIncomeAdjustmentService {
    IncomeAdjustmentDto create(IncomeAdjustmentDto dto);

    void update(IncomeAdjustmentDto dto);

    void delete(IncomeAdjustmentDto dto);

    IncomeAdjustmentDto findById(UUID id);

    PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria);

}

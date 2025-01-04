package com.kynsoft.finamer.creditcard.domain.services;

import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.creditcard.domain.dto.ManageBankReconciliationDto;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface IManageBankReconciliationService {

    ManageBankReconciliationDto create(ManageBankReconciliationDto dto);

    void update(ManageBankReconciliationDto dto);

    void delete(UUID id);

    ManageBankReconciliationDto findById(UUID id);

    PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria);

    void updateDetails(UUID bankReconciliationId);
}

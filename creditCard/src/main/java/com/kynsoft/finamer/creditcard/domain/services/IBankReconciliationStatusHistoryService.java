package com.kynsoft.finamer.creditcard.domain.services;

import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.creditcard.domain.dto.BankReconciliationStatusHistoryDto;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface IBankReconciliationStatusHistoryService {
    BankReconciliationStatusHistoryDto create(BankReconciliationStatusHistoryDto dto);

    void update(BankReconciliationStatusHistoryDto dto);

    void delete(UUID id);

    BankReconciliationStatusHistoryDto findById(UUID id);

    PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria);

}

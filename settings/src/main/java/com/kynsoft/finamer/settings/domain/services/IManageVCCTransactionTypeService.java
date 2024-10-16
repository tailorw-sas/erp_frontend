package com.kynsoft.finamer.settings.domain.services;

import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.settings.domain.dto.ManageVCCTransactionTypeDto;

import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface IManageVCCTransactionTypeService {
    UUID create(ManageVCCTransactionTypeDto dto);

    void update(ManageVCCTransactionTypeDto dto);

    void delete(ManageVCCTransactionTypeDto dto);

    ManageVCCTransactionTypeDto findById(UUID id);

    PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria);

    Long countByCodeAndNotId(String code, UUID id);

    List<ManageVCCTransactionTypeDto> findAllToReplicate();

    Long countByIsDefaultsAndNotId(UUID id);
}

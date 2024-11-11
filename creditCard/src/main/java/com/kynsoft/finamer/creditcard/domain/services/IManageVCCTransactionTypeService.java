package com.kynsoft.finamer.creditcard.domain.services;

import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.creditcard.domain.dto.ManageVCCTransactionTypeDto;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface IManageVCCTransactionTypeService {

    UUID create(ManageVCCTransactionTypeDto dto);

    void update(ManageVCCTransactionTypeDto dto);

    void delete(ManageVCCTransactionTypeDto dto);

    ManageVCCTransactionTypeDto findById(UUID id);

    List<ManageVCCTransactionTypeDto> findByIds(List<UUID> ids);

    List<ManageVCCTransactionTypeDto> findAll();

    PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria);

    ManageVCCTransactionTypeDto findByCode(String code);

    ManageVCCTransactionTypeDto findByIsDefaultAndNotIsSubcategory();

    ManageVCCTransactionTypeDto findByIsDefaultAndIsSubcategory();

    ManageVCCTransactionTypeDto findByManual();
}

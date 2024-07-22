package com.kynsoft.finamer.settings.domain.services;

import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.settings.domain.dto.ManageInvoiceTransactionTypeDto;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface IManageInvoiceTransactionTypeService {

    UUID create(ManageInvoiceTransactionTypeDto dto);

    void update(ManageInvoiceTransactionTypeDto dto);

    void delete(ManageInvoiceTransactionTypeDto dto);

    ManageInvoiceTransactionTypeDto findById(UUID id);

    PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria);

    Long countByCodeAndNotId(String code, UUID id);

    List<ManageInvoiceTransactionTypeDto> findAllToReplicate();
}

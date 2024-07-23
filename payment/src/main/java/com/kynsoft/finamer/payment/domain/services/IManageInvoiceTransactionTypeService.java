package com.kynsoft.finamer.payment.domain.services;

import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.payment.domain.dto.ManageInvoiceTransactionTypeDto;
import java.util.List;

import java.util.UUID;
import org.springframework.data.domain.Pageable;

public interface IManageInvoiceTransactionTypeService {

    UUID create(ManageInvoiceTransactionTypeDto dto);

    void update(ManageInvoiceTransactionTypeDto dto);

    void delete(ManageInvoiceTransactionTypeDto dto);

    ManageInvoiceTransactionTypeDto findById(UUID id);

    Long countByCodeAndNotId(String code, UUID id);

    PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria);
}

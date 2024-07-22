package com.kynsoft.finamer.settings.domain.services;

import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.settings.domain.dto.ManageInvoiceTypeDto;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface IManageInvoiceTypeService {

    UUID create(ManageInvoiceTypeDto dto);

    void update(ManageInvoiceTypeDto dto);

    void delete(ManageInvoiceTypeDto dto);

    ManageInvoiceTypeDto findById(UUID id);

    PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria);

    Long countByCodeAndNotId(String code, UUID id);

    List<ManageInvoiceTypeDto> findAllToReplicate();
}

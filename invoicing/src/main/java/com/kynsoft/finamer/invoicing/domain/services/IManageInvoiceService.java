package com.kynsoft.finamer.invoicing.domain.services;

import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.invoicing.domain.dto.ManageInvoiceDto;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface IManageInvoiceService {

    UUID create(ManageInvoiceDto dto);

    void update(ManageInvoiceDto dto);

    void delete(ManageInvoiceDto dto);

    ManageInvoiceDto findById(UUID id);

    PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria);

    List<ManageInvoiceDto> findByIds(List<UUID> ids);
}

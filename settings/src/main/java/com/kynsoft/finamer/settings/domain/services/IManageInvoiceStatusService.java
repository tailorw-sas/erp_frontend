package com.kynsoft.finamer.settings.domain.services;

import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.settings.domain.dto.ManageInvoiceStatusDto;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface IManageInvoiceStatusService {
    UUID create(ManageInvoiceStatusDto dto);

    void update(ManageInvoiceStatusDto dto);

    void delete(ManageInvoiceStatusDto dto);

    ManageInvoiceStatusDto findById(UUID id);

    PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria);

    Long countByCodeAndNotId(String code, UUID id);

    List<ManageInvoiceStatusDto> findByIds(List<UUID> ids);
}

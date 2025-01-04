package com.kynsoft.finamer.payment.domain.services;

import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.payment.domain.dto.ManageInvoiceStatusDto;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface IManageInvoiceStatusService {
    UUID create(ManageInvoiceStatusDto dto);

    void update(ManageInvoiceStatusDto dto);

    void delete(ManageInvoiceStatusDto dto);

    ManageInvoiceStatusDto findById(UUID id);

    ManageInvoiceStatusDto findByCode(String code);

    Long countByCodeAndNotId(String code, UUID id);

    List<ManageInvoiceStatusDto> findByIds(List<UUID> ids);

    PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria);
}

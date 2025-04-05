package com.kynsoft.finamer.payment.domain.services;

import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.payment.domain.dto.ManageInvoiceDto;
import com.kynsoft.finamer.payment.infrastructure.identity.Invoice;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface IManageInvoiceService {
    void create(ManageInvoiceDto dto);

    void update(ManageInvoiceDto dto);

    ManageInvoiceDto findById(UUID id);

    PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria);
    
    void deleteAll();

    List<ManageInvoiceDto> findByIdIn(List<UUID> ids);

    List<Invoice> findInvoiceWithEntityGraphByIdIn(List<UUID> ids);

    List<ManageInvoiceDto> findInvoicesByGenId(List<Long> ids);
}

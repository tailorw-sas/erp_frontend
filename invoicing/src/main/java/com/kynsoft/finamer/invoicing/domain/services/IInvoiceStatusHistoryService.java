package com.kynsoft.finamer.invoicing.domain.services;

import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.invoicing.domain.dto.InvoiceStatusHistoryDto;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface IInvoiceStatusHistoryService {
    UUID create(InvoiceStatusHistoryDto dto);

    void update(InvoiceStatusHistoryDto dto);



    void delete(InvoiceStatusHistoryDto dto);

    InvoiceStatusHistoryDto findById(UUID id);

    PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria);



}


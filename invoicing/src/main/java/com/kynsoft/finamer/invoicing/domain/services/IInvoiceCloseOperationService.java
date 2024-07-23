package com.kynsoft.finamer.invoicing.domain.services;

import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.invoicing.domain.dto.InvoiceCloseOperationDto;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface IInvoiceCloseOperationService {
    UUID create(InvoiceCloseOperationDto dto);

    void update(InvoiceCloseOperationDto dto);

    void updateAll(List<InvoiceCloseOperationDto> dtos);

    void delete(InvoiceCloseOperationDto dto);

    InvoiceCloseOperationDto findById(UUID id);

    PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria);

    Long findByHotelId(UUID hotelId);

    List<InvoiceCloseOperationDto> findByHotelIds(List<UUID> hotelIds);

    InvoiceCloseOperationDto findActiveByHotelId(UUID hotelId);
}

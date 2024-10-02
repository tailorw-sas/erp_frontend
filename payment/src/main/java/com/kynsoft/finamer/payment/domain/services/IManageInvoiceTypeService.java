package com.kynsoft.finamer.payment.domain.services;

import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.payment.domain.dto.ManageInvoiceTypeDto;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface IManageInvoiceTypeService {

    UUID create(ManageInvoiceTypeDto dto);

    void update(ManageInvoiceTypeDto dto);

    void delete(ManageInvoiceTypeDto dto);

    ManageInvoiceTypeDto findById(UUID id);

    Long countByCodeAndNotId(String code, UUID id);

    PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria);

}

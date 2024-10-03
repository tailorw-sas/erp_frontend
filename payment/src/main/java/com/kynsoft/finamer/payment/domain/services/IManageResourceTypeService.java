package com.kynsoft.finamer.payment.domain.services;

import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.payment.domain.dto.ResourceTypeDto;
import jakarta.annotation.Resource;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface IManageResourceTypeService {
    UUID create(ResourceTypeDto dto);

    void update(ResourceTypeDto dto);

    void delete(ResourceTypeDto dto);

    ResourceTypeDto findById(UUID id);

    ResourceTypeDto findByCode(String code);

    PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria);

    Long countByCodeAndNotId(String code, UUID id);

    Long countByDefaultAndNotId(UUID id);

  List<ResourceTypeDto> findAllToReplicate();

  Long countByInvoiceAndNotId(UUID id);
}

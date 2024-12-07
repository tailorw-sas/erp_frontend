package com.kynsoft.finamer.invoicing.domain.services;

import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.invoicing.domain.dto.ManageClientDto;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface IManagerClientService {
    UUID create(ManageClientDto dto);

    void update(ManageClientDto dto);

    void delete(ManageClientDto dto);

    ManageClientDto findById(UUID id);

    Long countByCodeAndNotId(String code, UUID id);

    PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria);
}

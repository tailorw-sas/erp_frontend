package com.kynsoft.finamer.settings.domain.services;

import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.settings.domain.dto.ManagerCountryDto;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface IManagerCountryService {
    UUID create(ManagerCountryDto dto);

    void update(ManagerCountryDto dto);

    void delete(ManagerCountryDto dto);

    ManagerCountryDto findById(UUID id);

    PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria);

    Long countByCodeAndNotId(String code, UUID id);

    Long countByNameAndNotId(String name, UUID id);

    List<ManagerCountryDto> findAllToReplicate();
}

package com.kynsoft.finamer.settings.domain.services;

import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.settings.domain.dto.ManageTradingCompaniesDto;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface IManageTradingCompaniesService {

    UUID create(ManageTradingCompaniesDto dto);

    void update(ManageTradingCompaniesDto dto);

    void delete(ManageTradingCompaniesDto dto);

    ManageTradingCompaniesDto findById(UUID id);

    PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria);

    Long countByCodeAndNotId(String code, UUID id);

    List<ManageTradingCompaniesDto> findByIds(List<UUID> ids);

    List<ManageTradingCompaniesDto> findAll();
}

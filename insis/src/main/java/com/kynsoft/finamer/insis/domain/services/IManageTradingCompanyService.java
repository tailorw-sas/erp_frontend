package com.kynsoft.finamer.insis.domain.services;

import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.insis.domain.dto.ManageTradingCompanyDto;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface IManageTradingCompanyService {

    UUID create(ManageTradingCompanyDto dto);

    void update(ManageTradingCompanyDto dto);

    void delete(ManageTradingCompanyDto dto);

    ManageTradingCompanyDto findById(UUID id);

    List<ManageTradingCompanyDto> findAll();

    PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria);
}

package com.kynsoft.finamer.insis.domain.services;

import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.insis.domain.dto.InnsistConnectionParamsDto;
import com.kynsoft.finamer.insis.domain.dto.ManageTradingCompanyDto;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface IInnsistConnectionParamsService {
    UUID create(InnsistConnectionParamsDto dto);

    void update(InnsistConnectionParamsDto dto);

    void delete(InnsistConnectionParamsDto dto);

    InnsistConnectionParamsDto findById(UUID id);

    InnsistConnectionParamsDto findByTradingCompany(UUID tradingCompanyId);

    List<InnsistConnectionParamsDto> findAll();

    PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria);

    boolean hasTradingCompanyAssociation(UUID id);

    ManageTradingCompanyDto findTradingCompanyAssociated(UUID id);
}

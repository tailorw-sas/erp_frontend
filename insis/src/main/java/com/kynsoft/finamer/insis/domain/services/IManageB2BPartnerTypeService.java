package com.kynsoft.finamer.insis.domain.services;

import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.insis.domain.dto.ManageB2BPartnerTypeDto;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface IManageB2BPartnerTypeService {
    UUID create(ManageB2BPartnerTypeDto dto);

    void update(ManageB2BPartnerTypeDto dto);

    void delete(ManageB2BPartnerTypeDto dto);

    ManageB2BPartnerTypeDto findById(UUID id);

    List<ManageB2BPartnerTypeDto> findAll();

    PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria);
}

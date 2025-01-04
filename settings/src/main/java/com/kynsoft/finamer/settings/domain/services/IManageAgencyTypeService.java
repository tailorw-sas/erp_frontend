package com.kynsoft.finamer.settings.domain.services;


import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.settings.domain.dto.ManageAgencyTypeDto;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface IManageAgencyTypeService {

    UUID create(ManageAgencyTypeDto dto);

    void update(ManageAgencyTypeDto dto);

    void delete(ManageAgencyTypeDto dto);

    ManageAgencyTypeDto findById(UUID id);

    PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria);

    Long countByCodeAndNotId(String code, UUID id);

    List<ManageAgencyTypeDto> findAllToReplicate();
}

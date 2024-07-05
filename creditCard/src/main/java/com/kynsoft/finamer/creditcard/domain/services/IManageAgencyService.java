package com.kynsoft.finamer.creditcard.domain.services;


import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.creditcard.domain.dto.ManageAgencyDto;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface IManageAgencyService {

    UUID create(ManageAgencyDto dto);

    void update(ManageAgencyDto dto);

    void delete(UUID id);

    ManageAgencyDto findById(UUID id);

    List<ManageAgencyDto> findByIds(List<UUID> ids);

    List<ManageAgencyDto> findAll();

    PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria);
}

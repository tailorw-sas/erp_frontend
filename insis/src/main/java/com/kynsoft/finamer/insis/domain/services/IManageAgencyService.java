package com.kynsoft.finamer.insis.domain.services;

import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.insis.domain.dto.ManageAgencyDto;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public interface IManageAgencyService {
    UUID create(ManageAgencyDto dto);

    void createMany(List<ManageAgencyDto> agencyDtos);

    void update(ManageAgencyDto dto);

    void delete(ManageAgencyDto dto);

    ManageAgencyDto findById(UUID id);

    ManageAgencyDto findByCode(String code);

    List<ManageAgencyDto> findAll();

    List<ManageAgencyDto> findByIds(List<UUID> ids);

    Map<String, UUID> findIdsByCodes(List<String> codes);

    List<ManageAgencyDto> findByCodes(List<String> codes);

    PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria);
}

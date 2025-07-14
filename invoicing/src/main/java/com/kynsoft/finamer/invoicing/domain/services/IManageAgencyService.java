package com.kynsoft.finamer.invoicing.domain.services;


import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.invoicing.domain.dto.ManageAgencyDto;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.data.domain.Pageable;

public interface IManageAgencyService {

    UUID create(ManageAgencyDto dto);

    void update(ManageAgencyDto dto);

    void delete(ManageAgencyDto dto);

    ManageAgencyDto findById(UUID id);

    ManageAgencyDto findByCode(String code);

    Long countByCodeAndNotId(String code, UUID id);

    List<ManageAgencyDto> findByIds(List<UUID> ids);

    boolean existByCode(String manageAgencyCode);

    PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria);

    void clearManageHotelCache();

    Map<UUID, ManageAgencyDto> getMapById(List<UUID> agencyIds);

    Map<String, ManageAgencyDto> getMapByCode(List<String> agencyCodes);
}

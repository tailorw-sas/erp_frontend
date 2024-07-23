package com.kynsoft.finamer.creditcard.domain.services;

import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.creditcard.domain.dto.ManageCollectionStatusDto;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface IManageCollectionStatusService {

    UUID create(ManageCollectionStatusDto dto);

    void update(ManageCollectionStatusDto dto);

    void delete(ManageCollectionStatusDto dto);

    ManageCollectionStatusDto findById(UUID id);

    PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria);

    Long countByCodeAndNotId(String code, UUID id);

    List<ManageCollectionStatusDto> findByIds(List<UUID> ids);

    Long countByNameAndNotId(String name, UUID id);

    ManageCollectionStatusDto findByCode(String code);
}

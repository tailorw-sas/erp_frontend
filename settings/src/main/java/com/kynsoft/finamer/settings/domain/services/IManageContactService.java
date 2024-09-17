package com.kynsoft.finamer.settings.domain.services;

import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.settings.domain.dto.ManageContactDto;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface IManageContactService {

    UUID create(ManageContactDto dto);

    void update(ManageContactDto dto);

    void delete(ManageContactDto dto);

    ManageContactDto findById(UUID id);

    PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria);

    Long countByCodeAndManageHotelIdAndNotId(String code, UUID manageHotelId, UUID id);

    Long countByEmailAndManageHotelIdAndNotId(String email, UUID manageHotelId, UUID id);

    List<ManageContactDto> findAllToReplicate();
}

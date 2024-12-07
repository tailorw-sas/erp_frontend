package com.kynsoft.finamer.creditcard.domain.services;

import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.creditcard.domain.dto.ManageAttachmentTypeDto;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface IManageAttachmentTypeService {

    UUID create(ManageAttachmentTypeDto dto);

    void update(ManageAttachmentTypeDto dto);

    void delete(ManageAttachmentTypeDto dto);

    ManageAttachmentTypeDto findById(UUID id);

    PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria);

    Long countByCodeAndNotId(String code, UUID id);

    Long countByDefaultAndNotId(UUID id);

    ManageAttachmentTypeDto findByDefault();
}

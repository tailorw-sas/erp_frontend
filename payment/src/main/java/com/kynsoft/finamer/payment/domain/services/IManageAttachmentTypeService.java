package com.kynsoft.finamer.payment.domain.services;

import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.payment.domain.dto.AttachmentTypeDto;
import java.util.List;

import java.util.UUID;
import org.springframework.data.domain.Pageable;

public interface IManageAttachmentTypeService {
    UUID create(AttachmentTypeDto dto);

    void update(AttachmentTypeDto dto);

    void delete(AttachmentTypeDto dto);

    AttachmentTypeDto findById(UUID id);

    AttachmentTypeDto findByCode(String code);

    PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria);

    Long countByCodeAndNotId(String code, UUID id);

    Long countByDefaultAndNotId(UUID id);

    Long countByAntiToIncomeImportAndNotId(UUID id);
}

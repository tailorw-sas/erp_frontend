package com.kynsoft.finamer.invoicing.domain.services;

import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.invoicing.domain.dto.ManageAttachmentDto;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Pageable;

public interface IManageAttachmentService {

    Long create(ManageAttachmentDto dto);

    void update(ManageAttachmentDto dto);

    void delete(ManageAttachmentDto dto);

    PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria);

    ManageAttachmentDto findById(UUID id);

    List<ManageAttachmentDto> findByIds(List<UUID> ids);

    void create(List<ManageAttachmentDto> dtos);
}

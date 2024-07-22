package com.kynsoft.finamer.settings.domain.services;

import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.settings.domain.dto.ManagePaymentAttachmentStatusDto;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface IManagePaymentAttachmentStatusService {
    UUID create(ManagePaymentAttachmentStatusDto dto);
    void update(ManagePaymentAttachmentStatusDto dto);
    void delete(ManagePaymentAttachmentStatusDto dto);
    ManagePaymentAttachmentStatusDto findById(UUID id);
    PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria);
    Long countByCode(String code, UUID id);
    List<ManagePaymentAttachmentStatusDto> findByIds(List<UUID> ids);
    Long countByNameAndNotId(String name, UUID id);
    List<ManagePaymentAttachmentStatusDto> findAllToReplicate();
}

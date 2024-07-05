package com.kynsoft.finamer.payment.domain.services;

import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.payment.domain.dto.AttachmentStatusHistoryDto;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface IAttachmentStatusHistoryService {
    UUID create(AttachmentStatusHistoryDto dto);

    void update(AttachmentStatusHistoryDto dto);

    void delete(AttachmentStatusHistoryDto dto);

    AttachmentStatusHistoryDto findById(UUID id);

    PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria);

}

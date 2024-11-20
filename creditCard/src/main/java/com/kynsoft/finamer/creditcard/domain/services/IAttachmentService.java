package com.kynsoft.finamer.creditcard.domain.services;

import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.creditcard.domain.dto.AttachmentDto;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface IAttachmentService {

    AttachmentDto create(AttachmentDto dto);

    void update(AttachmentDto dto);

    void delete(AttachmentDto dto);

    PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria);

    AttachmentDto findById(UUID id);

    List<AttachmentDto> findByIds(List<UUID> ids);

    List<AttachmentDto> findAllByTransactionId(Long invoiceId);

    void create(List<AttachmentDto> dtos);
}

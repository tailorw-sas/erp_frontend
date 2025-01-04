package com.kynsoft.finamer.payment.domain.services;

import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.payment.domain.dto.MasterPaymentAttachmentDto;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface IMasterPaymentAttachmentService {
    Long create(MasterPaymentAttachmentDto dto);

    void create(List<MasterPaymentAttachmentDto> dtos);

    void update(MasterPaymentAttachmentDto dto);

    void delete(MasterPaymentAttachmentDto dto);

    MasterPaymentAttachmentDto findById(UUID id);

    PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria);

    Long countByResourceAndAttachmentTypeIsDefault(UUID resource);

    List<MasterPaymentAttachmentDto> findAllByPayment(UUID payment);

    Long findMaxId();
}

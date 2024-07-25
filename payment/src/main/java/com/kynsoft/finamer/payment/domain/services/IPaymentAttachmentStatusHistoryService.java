package com.kynsoft.finamer.payment.domain.services;

import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.payment.domain.dto.PaymentAttachmentStatusHistoryDto;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface IPaymentAttachmentStatusHistoryService {
    UUID create(PaymentAttachmentStatusHistoryDto dto);

    void update(PaymentAttachmentStatusHistoryDto dto);

    void delete(PaymentAttachmentStatusHistoryDto dto);

    PaymentAttachmentStatusHistoryDto findById(UUID id);

    PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria);

}

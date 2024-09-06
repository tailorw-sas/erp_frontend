package com.kynsoft.finamer.payment.domain.services;

import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.payment.domain.dto.ManagePaymentAttachmentStatusDto;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Pageable;

public interface IManagePaymentAttachmentStatusService {

    UUID create(ManagePaymentAttachmentStatusDto dto);

    void update(ManagePaymentAttachmentStatusDto dto);

    void delete(ManagePaymentAttachmentStatusDto dto);

    ManagePaymentAttachmentStatusDto findById(UUID id);

    PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria);

    ManagePaymentAttachmentStatusDto findByDefaults();
}

package com.kynsoft.finamer.payment.domain.services;

import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.payment.domain.dto.ManagePaymentAttachmentStatusDto;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface IManagePaymentAttachmentStatusService {

    UUID create(ManagePaymentAttachmentStatusDto dto);

    void update(ManagePaymentAttachmentStatusDto dto);

    void delete(ManagePaymentAttachmentStatusDto dto);

    ManagePaymentAttachmentStatusDto findById(UUID id);

    PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria);

    ManagePaymentAttachmentStatusDto findByDefaults();

    ManagePaymentAttachmentStatusDto findByCode(String code);

    ManagePaymentAttachmentStatusDto findByNonNone();

    ManagePaymentAttachmentStatusDto findByPatWithAttachment();

    ManagePaymentAttachmentStatusDto findByPwaWithOutAttachment();

    ManagePaymentAttachmentStatusDto findBySupported();
}

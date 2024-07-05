package com.kynsoft.finamer.payment.domain.services;

import com.kynsoft.finamer.payment.domain.dto.ManagePaymentAttachmentStatusDto;
import java.util.UUID;

public interface IManagePaymentAttachmentStatusService {

    UUID create(ManagePaymentAttachmentStatusDto dto);

    void update(ManagePaymentAttachmentStatusDto dto);

    void delete(ManagePaymentAttachmentStatusDto dto);

    ManagePaymentAttachmentStatusDto findById(UUID id);
}

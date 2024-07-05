package com.kynsoft.finamer.payment.domain.services;

import com.kynsoft.finamer.payment.domain.dto.ManageAttachmentTypeDto;

import java.util.UUID;

public interface IManageAttachmentTypeService {
    UUID create(ManageAttachmentTypeDto dto);

    void update(ManageAttachmentTypeDto dto);

    void delete(ManageAttachmentTypeDto dto);

    ManageAttachmentTypeDto findById(UUID id);

}

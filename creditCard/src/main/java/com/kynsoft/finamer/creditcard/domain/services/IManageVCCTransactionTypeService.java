package com.kynsoft.finamer.creditcard.domain.services;

import com.kynsoft.finamer.creditcard.domain.dto.ManageVCCTransactionTypeDto;

import java.util.List;
import java.util.UUID;

public interface IManageVCCTransactionTypeService {

    UUID create(ManageVCCTransactionTypeDto dto);

    void update(ManageVCCTransactionTypeDto dto);

    void delete(ManageVCCTransactionTypeDto dto);

    ManageVCCTransactionTypeDto findById(UUID id);

    List<ManageVCCTransactionTypeDto> findByIds(List<UUID> ids);

    List<ManageVCCTransactionTypeDto> findAll();
}

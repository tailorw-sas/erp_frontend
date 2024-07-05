package com.kynsoft.finamer.creditcard.domain.services;

import com.kynsoft.finamer.creditcard.domain.dto.ManageTransactionStatusDto;

import java.util.List;
import java.util.UUID;

public interface IManageTransactionStatusService {
    UUID create(ManageTransactionStatusDto dto);

    void update(ManageTransactionStatusDto dto);

    void delete(ManageTransactionStatusDto dto);

    ManageTransactionStatusDto findById(UUID id);

    List<ManageTransactionStatusDto> findByIds(List<UUID> ids);
}

package com.kynsoft.finamer.creditcard.domain.services;

import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.creditcard.domain.dto.ManageReconcileTransactionStatusDto;
import com.kynsoft.finamer.creditcard.domain.dtoEnum.EReconcileTransactionStatus;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface IManageReconcileTransactionStatusService {
    UUID create(ManageReconcileTransactionStatusDto dto);

    void update(ManageReconcileTransactionStatusDto dto);

    void delete(ManageReconcileTransactionStatusDto dto);

    ManageReconcileTransactionStatusDto findById(UUID id);

    PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria);

    Long countByCodeAndNotId(String code, UUID id);

    List<ManageReconcileTransactionStatusDto> findByIds(List<UUID> ids);

    Long countByCreatedAndNotId(UUID id);

    Long countByCancelledAndNotId(UUID id);

    Long countByCompletedAndNotId(UUID id);

    ManageReconcileTransactionStatusDto findByEReconcileTransactionStatus(EReconcileTransactionStatus transactionStatus);
}

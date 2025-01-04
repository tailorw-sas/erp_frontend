package com.kynsoft.finamer.settings.domain.services;

import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.settings.domain.dto.ManageTransactionStatusDto;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface IManageTransactionStatusService {
    UUID create(ManageTransactionStatusDto dto);

    void update(ManageTransactionStatusDto dto);

    void delete(ManageTransactionStatusDto dto);

    ManageTransactionStatusDto findById(UUID id);

    List<ManageTransactionStatusDto> findByIds(List<UUID> ids);

    PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria);

    Long countByCodeAndNotId(String code, UUID id);

    List<ManageTransactionStatusDto> findAllToReplicate();
}

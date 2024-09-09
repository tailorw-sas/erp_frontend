package com.kynsoft.finamer.settings.domain.services;

import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.settings.domain.dto.ManagerPaymentStatusDto;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface IManagerPaymentStatusService {
    UUID create(ManagerPaymentStatusDto dto);
    void update(ManagerPaymentStatusDto dto);
    void delete(ManagerPaymentStatusDto dto);
    ManagerPaymentStatusDto findById(UUID uuid);
    PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria);
    Long countByCode(String code, UUID id);
    List<ManagerPaymentStatusDto> findAllToReplicate();
    Long countByAppliedAndNotId(UUID id);
}

package com.kynsoft.finamer.settings.domain.services;

import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.settings.domain.dto.ManagePaymentTransactionStatusDto;

import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface IManagePaymentTransactionStatusService {
    UUID create(ManagePaymentTransactionStatusDto dto);

    void update(ManagePaymentTransactionStatusDto dto);

    void delete(ManagePaymentTransactionStatusDto dto);

    ManagePaymentTransactionStatusDto findById(UUID id);

    PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria);

    Long countByCodeAndNotId(String code, UUID id);
}

package com.kynsoft.finamer.settings.domain.services;

import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.settings.domain.dto.ManagePaymentTransactionTypeDto;

import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface IManagePaymentTransactionTypeService {
    UUID create(ManagePaymentTransactionTypeDto dto);

    void update(ManagePaymentTransactionTypeDto dto);

    void delete(ManagePaymentTransactionTypeDto dto);

    ManagePaymentTransactionTypeDto findById(UUID id);

    PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria);

    PaginatedResponse findWithApplyDepositAndDepositFalse(Boolean applyDeposit, Boolean deposit, Boolean defaults, Pageable pageable);

    Long countByCodeAndNotId(String code, UUID id);

    Long countByDefaultAndNotId(UUID id);

    List<ManagePaymentTransactionTypeDto> findAllToReplicate();

    Long countByIncomeDefaultAndNotId(UUID id);

    Long countByApplyDepositAndNotId(UUID id);

    Long countByDepositAndNotId(UUID id);
}

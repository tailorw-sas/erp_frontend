package com.kynsoft.finamer.settings.domain.services;

import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.settings.domain.dto.ManageMerchantCommissionDto;
import java.time.LocalDate;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface IManageMerchantCommissionService {
    UUID create(ManageMerchantCommissionDto dto);

    void update(ManageMerchantCommissionDto dto);

    void delete(ManageMerchantCommissionDto dto);

    ManageMerchantCommissionDto findById(UUID id);

    PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria);

    List<ManageMerchantCommissionDto> findAllByMerchantAndCreditCardType(UUID managerMerchant, UUID manageCreditCartType);

    boolean checkDateOverlapForSameCombination(UUID managerMerchant, UUID manageCreditCartType, Double commission, String calculationType, LocalDate fromDate, LocalDate toDate);

    boolean checkDateOverlapForDifferentCombination(UUID managerMerchant, UUID manageCreditCartType, LocalDate fromDate, LocalDate toDate);

    boolean hasOverlappingRecords(UUID id, UUID managerMerchant, UUID manageCreditCartType, LocalDate fromDate, LocalDate toDate);
}
package com.kynsoft.finamer.creditcard.domain.services;

import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.creditcard.domain.dto.ManageMerchantCommissionDto;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface IManageMerchantCommissionService {
    UUID create(ManageMerchantCommissionDto dto);

    void update(ManageMerchantCommissionDto dto);

    void delete(ManageMerchantCommissionDto dto);

    ManageMerchantCommissionDto findById(UUID id);

    PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria);

    List<ManageMerchantCommissionDto> findAllByMerchantAndCreditCardType(UUID managerMerchant, UUID manageCreditCartType);

    ManageMerchantCommissionDto findByManagerMerchantAndManageCreditCartTypeAndDateWithinRangeOrNoEndDate(UUID managerMerchant, UUID manageCreditCartType, LocalDate date);

    Double calculateCommission(double amount, UUID merchantId, UUID creditCardTypeId, LocalDate date, int decimals);

    boolean checkDateOverlapForSameCombination(UUID managerMerchant, UUID manageCreditCartType, Double commission, String calculationType, LocalDate fromDate, LocalDate toDate);

    boolean checkDateOverlapForDifferentCombination(UUID managerMerchant, UUID manageCreditCartType, LocalDate fromDate, LocalDate toDate);

    boolean hasOverlappingRecords(UUID id, UUID managerMerchant, UUID manageCreditCartType, LocalDate fromDate, LocalDate toDate, Double commission, String calculationType);

    List<ManageMerchantCommissionDto> findAllByMerchantAndCreditCardTypeById(UUID managerMerchant, UUID manageCreditCartType, UUID id);
}
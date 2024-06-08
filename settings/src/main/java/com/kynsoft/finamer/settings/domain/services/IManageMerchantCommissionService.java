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

    Long countByManagerMerchantANDManagerCreditCartType(UUID managerMerchant, UUID manageCreditCartType);

    Long countByManagerMerchantANDManagerCreditCartTypeIdNotId(UUID id, UUID managerMerchant, UUID manageCreditCartType);

    Long countByManagerMerchantANDManagerCreditCartTypeANDDateRange(UUID id, UUID managerMerchant,
            UUID manageCreditCartType,
            LocalDate fromCheckDate,
            LocalDate toCheckDate);

}

package com.kynsoft.finamer.creditcard.domain.services;

import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.creditcard.domain.dto.ManagerMerchantConfigDto;
import com.kynsoft.finamer.creditcard.domain.dto.ManagerMerchantConfigResponseDto;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface IManageMerchantConfigService {
    UUID create(ManagerMerchantConfigDto dto);
    ManagerMerchantConfigResponseDto findByIdWithDate(UUID id);
    ManagerMerchantConfigDto findById(UUID id);
    PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria);
    Long countByCodeAndNotId(UUID managerMerchant);
    Long countByManagerMerchantANDManagerCurrencyIdNotId(UUID id, UUID managerMerchant);
    void delete(ManagerMerchantConfigDto dto);
    void update(ManagerMerchantConfigDto dto);
    ManagerMerchantConfigDto findByMerchantID(UUID id);
}

package com.kynsoft.finamer.settings.domain.services;

import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.settings.domain.dto.ManagerMerchantConfigDto;
import com.kynsoft.finamer.settings.domain.dto.me.ManagerMerchantConfigResponseDto;
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
}

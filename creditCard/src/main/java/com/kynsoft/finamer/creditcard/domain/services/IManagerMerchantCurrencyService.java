package com.kynsoft.finamer.creditcard.domain.services;

import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.creditcard.domain.dto.ManagerMerchantCurrencyDto;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface IManagerMerchantCurrencyService {
    UUID create(ManagerMerchantCurrencyDto dto);

    void update(ManagerMerchantCurrencyDto dto);

    void delete(ManagerMerchantCurrencyDto dto);

    ManagerMerchantCurrencyDto findById(UUID id);

    PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria);

    Long countByCodeAndNotId(UUID managerMerchant, UUID managerCurrency);

    Long countByManagerMerchantANDManagerCurrencyIdNotId(UUID id, UUID managerMerchant, UUID managerCurrency);
}

package com.kynsoft.finamer.creditcard.domain.services;

import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.creditcard.domain.dto.ManageMerchantBankAccountDto;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface IManageMerchantBankAccountService {
    UUID create(ManageMerchantBankAccountDto dto);

    void update(ManageMerchantBankAccountDto dto);

    void delete(ManageMerchantBankAccountDto dto);

    ManageMerchantBankAccountDto findById(UUID id);

    PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria);

    Long countByManagerMerchantANDManagerCurrencyIdNotId(UUID id, UUID managerMerchant, UUID manageBank, String accountNumber);
}

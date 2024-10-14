package com.kynsoft.finamer.creditcard.domain.services;

import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.creditcard.domain.dto.ManageLanguageDto;
import com.kynsoft.finamer.creditcard.domain.dto.MerchantLanguageCodeDto;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface IMerchantLanguageCodeService {
    MerchantLanguageCodeDto create(MerchantLanguageCodeDto dto);

    void update(MerchantLanguageCodeDto dto);

    void delete(MerchantLanguageCodeDto dto);

    MerchantLanguageCodeDto findById(UUID id);

    PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria);

    Long countByManageMerchantAndMerchantLanguageAndNotId(UUID manageMerchant, UUID manageLanguage, UUID id);

    List<ManageLanguageDto> findManageLanguageByMerchantId(UUID merchantId);

    String findMerchantLanguageByMerchantIdAndLanguageId(UUID merchantId, UUID languageId);
}

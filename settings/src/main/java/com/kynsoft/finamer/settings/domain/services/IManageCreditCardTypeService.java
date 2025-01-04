package com.kynsoft.finamer.settings.domain.services;

import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.settings.domain.dto.ManageCreditCardTypeDto;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface IManageCreditCardTypeService {
    UUID create(ManageCreditCardTypeDto dto);

    void update(ManageCreditCardTypeDto dto);

    void delete(ManageCreditCardTypeDto dto);

    ManageCreditCardTypeDto findById(UUID id);

    PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria);

    Long countByCodeAndNotId(String code, UUID id);

    Long countByFirstDigitAndNotId(Integer firstDigit, UUID id);

    List<ManageCreditCardTypeDto> findAllToReplicate();
}

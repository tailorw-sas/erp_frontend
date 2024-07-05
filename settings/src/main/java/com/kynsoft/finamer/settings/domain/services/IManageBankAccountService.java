package com.kynsoft.finamer.settings.domain.services;

import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.settings.domain.dto.ManageBankAccountDto;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface IManageBankAccountService {

    UUID create(ManageBankAccountDto dto);

    void update(ManageBankAccountDto dto);

    void delete(ManageBankAccountDto dto);

    ManageBankAccountDto findById(UUID id);

    PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria);

    Long countByAccountNumberAndNotId(String accountNumber, UUID id);
}

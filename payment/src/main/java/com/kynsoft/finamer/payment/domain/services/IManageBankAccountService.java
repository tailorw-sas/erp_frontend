package com.kynsoft.finamer.payment.domain.services;

import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.payment.domain.dto.ManageBankAccountDto;
import java.util.List;

import java.util.UUID;
import org.springframework.data.domain.Pageable;

public interface IManageBankAccountService {

    UUID create(ManageBankAccountDto dto);

    void update(ManageBankAccountDto dto);

    void delete(ManageBankAccountDto dto);

    ManageBankAccountDto findById(UUID id);
    ManageBankAccountDto findByAccountNumber(String accountNumber);

    boolean existByAccountNumber(String accountNumber);
    PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria);

    List<ManageBankAccountDto> findAllByHotel(UUID hotelId);
}

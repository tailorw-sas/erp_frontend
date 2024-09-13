package com.kynsoft.finamer.creditcard.domain.services;

import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.creditcard.infrastructure.identity.ManagerB2BPartnerDto;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface IManagerB2BPartnerService {
    UUID create(ManagerB2BPartnerDto dto);

    void update(ManagerB2BPartnerDto dto);

    void delete(ManagerB2BPartnerDto dto);

    ManagerB2BPartnerDto findById(UUID id);

    PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria);

    Long countByCodeAndNotId(String code, UUID id);
}

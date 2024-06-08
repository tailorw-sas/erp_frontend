package com.kynsof.identity.domain.interfaces.service;

import com.kynsof.identity.domain.dto.CustomerDto;
import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface ICustomerService {

    void create(CustomerDto customer);

    void update(CustomerDto customer);

    void delete(CustomerDto customer);

    void deleteAll(List<UUID> customers);

    CustomerDto findById(UUID id);

    PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria);
}

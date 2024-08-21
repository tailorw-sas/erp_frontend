package com.kynsoft.report.domain.services;

import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.report.domain.dto.DBConectionDto;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface IDBConectionService {

    void create(DBConectionDto object);

    void update(DBConectionDto object);

    void delete(DBConectionDto object);

    PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria);

    DBConectionDto findById(UUID id);

    Long countByCodeAndNotId(String code, UUID id);
}

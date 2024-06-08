package com.kynsoft.notification.domain.service;

import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.notification.domain.dto.AdvertisingContentDto;
import java.util.List;

import java.util.UUID;
import org.springframework.data.domain.Pageable;

public interface IAdvertisingContentService {
    void create(AdvertisingContentDto object);
    void update(AdvertisingContentDto object);
    void delete(AdvertisingContentDto object);
    AdvertisingContentDto findById(UUID id);
    PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria);
}

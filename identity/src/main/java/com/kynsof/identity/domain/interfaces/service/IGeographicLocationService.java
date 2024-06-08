package com.kynsof.identity.domain.interfaces.service;

import com.kynsof.identity.domain.dto.GeographicLocationDto;
import com.kynsof.identity.domain.dto.LocationHierarchyDto;
import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface IGeographicLocationService {
    void create(GeographicLocationDto object);
    GeographicLocationDto findById(UUID id);
    void delete(GeographicLocationDto delete);

    PaginatedResponse findAll(Pageable pageable);
    LocationHierarchyDto findCantonAndProvinceIdsByParroquiaId(UUID parroquiaId);

    PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria);
}
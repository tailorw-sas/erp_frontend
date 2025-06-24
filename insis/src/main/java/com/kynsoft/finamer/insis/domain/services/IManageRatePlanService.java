package com.kynsoft.finamer.insis.domain.services;

import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.insis.domain.dto.ManageRatePlanDto;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface IManageRatePlanService {
    UUID create(ManageRatePlanDto dto);

    List<ManageRatePlanDto> createMany(List<ManageRatePlanDto> ratePlanDtos);

    void update(ManageRatePlanDto dto);

    void delete(ManageRatePlanDto dto);

    ManageRatePlanDto findById(UUID id);

    ManageRatePlanDto findByCode(String code);

    ManageRatePlanDto findByCodeAndHotel(String code, UUID hotel);

    List<ManageRatePlanDto> findAll();

    Map<String, UUID> findIdsByCodes(UUID hotelCode, List<String> codes);

    List<ManageRatePlanDto> findAllByCodesAndHotel(List<String> codes, UUID hotelId);

    PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria);
}

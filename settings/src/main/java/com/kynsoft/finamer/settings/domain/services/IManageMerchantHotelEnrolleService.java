package com.kynsoft.finamer.settings.domain.services;

import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.settings.domain.dto.ManageMerchantHotelEnrolleDto;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface IManageMerchantHotelEnrolleService {
    UUID create(ManageMerchantHotelEnrolleDto dto);

    void update(ManageMerchantHotelEnrolleDto dto);

    void delete(ManageMerchantHotelEnrolleDto dto);

    ManageMerchantHotelEnrolleDto findById(UUID id);

    PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria);

    Long countByManageMerchantAndManageHotelNotId(UUID id, UUID managerMerchant, UUID managerHotel);
}

package com.kynsoft.finamer.creditcard.domain.services;

import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.creditcard.domain.dto.ManageHotelDto;
import com.kynsoft.finamer.creditcard.domain.dto.ManageMerchantDto;
import com.kynsoft.finamer.creditcard.domain.dto.ManageMerchantHotelEnrolleDto;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface IManageMerchantHotelEnrolleService {
    UUID create(ManageMerchantHotelEnrolleDto dto);

    void update(ManageMerchantHotelEnrolleDto dto);

    void delete(ManageMerchantHotelEnrolleDto dto);

    ManageMerchantHotelEnrolleDto findById(UUID id);

    ManageMerchantHotelEnrolleDto findByManageMerchantAndManageHotel(ManageMerchantDto manageMerchantDto, ManageHotelDto manageHotelDto);

    PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria);

    PaginatedResponse findHotelsByManageMerchant(Pageable pageable, List<FilterCriteria> filterCriteria);
}

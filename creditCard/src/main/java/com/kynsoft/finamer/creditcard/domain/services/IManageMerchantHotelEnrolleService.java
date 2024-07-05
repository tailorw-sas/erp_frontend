package com.kynsoft.finamer.creditcard.domain.services;

import com.kynsoft.finamer.creditcard.domain.dto.ManageHotelDto;
import com.kynsoft.finamer.creditcard.domain.dto.ManageMerchantDto;
import com.kynsoft.finamer.creditcard.domain.dto.ManageMerchantHotelEnrolleDto;

import java.util.UUID;

public interface IManageMerchantHotelEnrolleService {
    UUID create(ManageMerchantHotelEnrolleDto dto);

    void update(ManageMerchantHotelEnrolleDto dto);

    void delete(ManageMerchantHotelEnrolleDto dto);

    ManageMerchantHotelEnrolleDto findById(UUID id);

    ManageMerchantHotelEnrolleDto findByManageMerchantAndManageHotel(ManageMerchantDto manageMerchantDto, ManageHotelDto manageHotelDto);
}

package com.kynsoft.finamer.payment.domain.services;

import com.kynsoft.finamer.payment.domain.dto.ManageHotelDto;

import java.util.UUID;

public interface IManageHotelService {

    UUID create(ManageHotelDto dto);

    void update(ManageHotelDto dto);

    void delete(ManageHotelDto dto);

    ManageHotelDto findById(UUID id);
}

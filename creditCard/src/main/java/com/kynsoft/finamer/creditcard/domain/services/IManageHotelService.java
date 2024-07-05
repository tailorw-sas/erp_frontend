package com.kynsoft.finamer.creditcard.domain.services;

import com.kynsoft.finamer.creditcard.domain.dto.ManageHotelDto;

import java.util.List;
import java.util.UUID;

public interface IManageHotelService {
    UUID create(ManageHotelDto dto);

    void update(ManageHotelDto dto);

    void delete(ManageHotelDto dto);

    ManageHotelDto findById(UUID id);

    List<ManageHotelDto> findByIds(List<UUID> ids);

    List<ManageHotelDto> findAll();
}

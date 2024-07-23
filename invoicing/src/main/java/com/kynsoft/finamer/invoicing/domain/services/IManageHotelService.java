package com.kynsoft.finamer.invoicing.domain.services;

import com.kynsoft.finamer.invoicing.domain.dto.ManageHotelDto;

import java.util.List;
import java.util.UUID;

public interface IManageHotelService {

    UUID create(ManageHotelDto dto);

    void update(ManageHotelDto dto);

    void delete(ManageHotelDto dto);

    ManageHotelDto findById(UUID id);

    ManageHotelDto findByCode(String code);

    boolean existByCode(String code);

    Long countByCodeAndNotId(String code, UUID id);

    List<ManageHotelDto> findByIds(List<UUID> ids);
}

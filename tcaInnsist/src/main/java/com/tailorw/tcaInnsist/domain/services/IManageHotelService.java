package com.tailorw.tcaInnsist.domain.services;

import com.tailorw.tcaInnsist.domain.dto.ManageHotelDto;

import java.util.List;
import java.util.UUID;

public interface IManageHotelService {

    UUID create(ManageHotelDto manageHotelDto);

    void update(ManageHotelDto manageHotelDto);

    void delete(UUID id);

    void createMany(List<ManageHotelDto> hotels);

    ManageHotelDto getById(UUID id);

    ManageHotelDto getByCode(String code);

    List<ManageHotelDto> getAll();

    boolean existsHotels();

}

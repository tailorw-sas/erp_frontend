package com.kynsoft.finamer.insis.domain.services;

import com.kynsoft.finamer.insis.domain.dto.ManageRoomCategoryDto;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface IManageRoomCategoryService {
    UUID create(ManageRoomCategoryDto dto);

    List<ManageRoomCategoryDto> createMany(List<ManageRoomCategoryDto> roomCategoryDtos);

    void update(ManageRoomCategoryDto dto);

    void delete(UUID id);

    ManageRoomCategoryDto findById(UUID id);

    ManageRoomCategoryDto findByCode(String code);

    List<ManageRoomCategoryDto> findAll();

    Map<String, UUID> findIdsByCodes(List<String> codes);

    List<ManageRoomCategoryDto> findAllByCodes(List<String> codes);
}

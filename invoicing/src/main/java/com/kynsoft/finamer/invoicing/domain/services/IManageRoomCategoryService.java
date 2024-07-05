package com.kynsoft.finamer.invoicing.domain.services;


import com.kynsoft.finamer.invoicing.domain.dto.ManageRoomCategoryDto;

import java.util.UUID;

public interface IManageRoomCategoryService {

    UUID create(ManageRoomCategoryDto dto);

    void update(ManageRoomCategoryDto dto);

    void delete(ManageRoomCategoryDto dto);

    ManageRoomCategoryDto findById(UUID id);



    Long countByCodeAndNotId(String code, UUID id);
}

package com.kynsoft.finamer.invoicing.domain.services;

import com.kynsoft.finamer.invoicing.domain.dto.ManageNightTypeDto;

import java.util.UUID;

public interface IManageNightTypeService {
    UUID create(ManageNightTypeDto dto);

    void update(ManageNightTypeDto dto);

    void delete(ManageNightTypeDto dto);

    ManageNightTypeDto findById(UUID id);



    Long countByCodeAndNotId(String code, UUID id);
}

package com.kynsoft.finamer.invoicing.domain.services;

import com.kynsoft.finamer.invoicing.domain.dto.ManageNightTypeDto;

import java.util.UUID;

public interface IManageNightTypeService {
    UUID create(ManageNightTypeDto dto);

    void update(ManageNightTypeDto dto);

    void delete(ManageNightTypeDto dto);

    ManageNightTypeDto findById(UUID id);

    boolean existNightTypeByCode(String code);

    ManageNightTypeDto findByCode(String code);



    Long countByCodeAndNotId(String code, UUID id);
}

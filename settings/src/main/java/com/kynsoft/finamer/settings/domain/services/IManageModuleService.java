package com.kynsoft.finamer.settings.domain.services;

import com.kynsoft.finamer.settings.domain.dto.ManageModuleDto;

import java.util.UUID;

public interface IManageModuleService {

    UUID create(ManageModuleDto dto);

    void update(ManageModuleDto dto);

    void delete(ManageModuleDto dto);

    ManageModuleDto findById(UUID id);
}

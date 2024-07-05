package com.kynsoft.finamer.creditcard.domain.services;

import com.kynsoft.finamer.creditcard.domain.dto.ManageLanguageDto;

import java.util.UUID;

public interface IManageLanguageService {

    UUID create(ManageLanguageDto dto);

    void update(ManageLanguageDto dto);

    void delete(ManageLanguageDto dto);

    ManageLanguageDto findById(UUID id);
}

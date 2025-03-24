package com.kynsoft.finamer.payment.domain.services;

import com.kynsoft.finamer.payment.domain.dto.ManageLanguageDto;

import java.util.UUID;

public interface IManageLanguageService {

    UUID create(ManageLanguageDto dto);

    void update(ManageLanguageDto dto);

    ManageLanguageDto findById(UUID id);

    ManageLanguageDto findByCode(String code);
}

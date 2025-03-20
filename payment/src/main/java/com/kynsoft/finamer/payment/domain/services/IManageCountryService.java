package com.kynsoft.finamer.payment.domain.services;

import com.kynsoft.finamer.payment.domain.dto.ManageCountryDto;

import java.util.UUID;

public interface IManageCountryService {
    UUID create(ManageCountryDto dto);

    void update(ManageCountryDto dto);

    void delete(ManageCountryDto dto);

    ManageCountryDto findById(UUID id);

    Long countByCodeAndNotId(String code, UUID id);

    Long countByNameAndNotId(String name, UUID id);
}
